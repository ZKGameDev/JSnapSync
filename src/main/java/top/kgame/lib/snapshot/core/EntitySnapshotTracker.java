package top.kgame.lib.snapshot.core;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.kgame.lib.snapshot.SerializeComponent;
import top.kgame.lib.snapshot.SerializeEntity;

import java.util.ArrayList;

public class EntitySnapshotTracker {
    private static final Logger logger = LogManager.getLogger(EntitySnapshotTracker.class);
    private final int id;
    private int createSequence;
    private int updateSequence;
    private int destroySequence;
    private final SerializeEntity entity;
    private final ArrayList<ComponentSerializer> serializer = new ArrayList<>();
    private final SnapshotBuffer componentSnapshotBuffer = SnapshotBuffer.generate();
    private EntitySnapshotTracker(SerializeEntity entity) {
        this.id = entity.getGuid();
        this.entity = entity;
    }

    public static EntitySnapshotTracker generate(SerializeEntity entity) {
        EntitySnapshotTracker instance = new EntitySnapshotTracker(entity);
        for (SerializeComponent componentType : entity.getComponents()) {
            ComponentSerializer serializer = ComponentSerializer.generate(entity, componentType);
            instance.addSerializer(serializer);
        }
        return instance;
    }

    private void addSerializer(ComponentSerializer serializer) {
        this.serializer.add(serializer);
    }

    public int getCreateSequence() {
        return createSequence;
    }

    public void setCreateSequence(int createSequence) {
        this.createSequence = createSequence;
    }

    public int getDestroySequence() {
        return destroySequence;
    }

    public void setDestroySequence(int destroySequence) {
        this.destroySequence = destroySequence;
    }

    public int getId() {
        return id;
    }

    public void generateEntitySnapshot(int sequence) {
        Snapshot snapshot = new Snapshot(entity.getGuid(), entity.getTypeId());
        for (ComponentSerializer componentSerializer : serializer) {
            if (!snapshot.registerComponentData(componentSerializer)) {
                logger.error("generateEntitySnapshot failed in sequence[{}]! reason: registerComponentData failed! ReplicatedComponentInfo[{}]", sequence, componentSerializer);
            }
        }
        componentSnapshotBuffer.insert(sequence, snapshot);
    }

    public int getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(int updateSequence) {
        this.updateSequence = updateSequence;
    }

    public boolean hasSnapshot(int sequence) {
        return componentSnapshotBuffer.hasSnapshot(sequence);
    }

    public boolean compareSnapshotSame(int preSequence, int serverSequence) {
        Snapshot preSnapshot = componentSnapshotBuffer.getSnapshot(preSequence);
        Snapshot newSnapshot = componentSnapshotBuffer.getSnapshot(serverSequence);
        if (null == preSnapshot) {
            return false;
        }
        if (null == newSnapshot) {
            return true;
        }
        return newSnapshot.isSame(preSnapshot);
    }

    public byte[] getSnapshot(int sendSequence) {
        return componentSnapshotBuffer.getSnapshotByteData(sendSequence);
    }

    @Override
    public String toString() {
        return "ReplicateEntityInfo{" +
                "id=" + id +
                ", createSequence=" + createSequence +
                ", updateSequence=" + updateSequence +
                ", destroySequence=" + destroySequence +
                '}';
    }

    /**
     * 生成增量快照数据
     * @param baseLine 用于对比的基准快照Id
     * @param sendSequence 发送数据的快照id
     * @return 增量快照的完整byte数据。包括id type size等字段。 如果与基准快照完全一致则返回null
     */
    public byte[] generateAdditionSnapshot(int baseLine, int sendSequence) {
        Snapshot preSnapshot = componentSnapshotBuffer.getSnapshot(baseLine);
        Snapshot newSnapshot = componentSnapshotBuffer.getSnapshot(sendSequence);
        if (null == preSnapshot) {
            return newSnapshot.getFullSnapshot();
        }
        if (null == newSnapshot) {
            return null;
        }
        return newSnapshot.generateAdditionData(preSnapshot);
    }

    public SerializeEntity getEntity() {
        return entity;
    }
}

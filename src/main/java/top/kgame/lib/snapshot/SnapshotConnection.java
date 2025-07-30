package top.kgame.lib.snapshot;

import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.kgame.lib.snapshot.core.EntitySnapshotTracker;
import top.kgame.lib.snapshot.tools.SnapshotTools;
import top.kgame.lib.snapshot.tools.ReplicatedReader;
import top.kgame.lib.snapshot.tools.ReplicatedUtil;

import java.util.*;


public abstract class SnapshotConnection {
    private static final Logger logger = LogManager.getLogger(SnapshotConnection.class);

    private final DeserializeFactory deserializeFactory;

    private final long uid;
    private int inSequence;
    private int outSequence;
    private int maxSnapshotAck;

    public SnapshotConnection(long uid, DeserializeFactory deserializeFactory) {
        this.uid = uid;
        this.deserializeFactory = deserializeFactory;
    }

    public int getInSequence() {
        return inSequence;
    }

    public void setInSequence(int inSequence) {
        this.inSequence = inSequence;
    }

    public int getOutSequence() {
        return outSequence;
    }

    public void setOutSequence(int outSequence) {
        this.outSequence = outSequence;
    }

    public void sendPackage(ByteBuf output, int serverSequence, Map<Integer, EntitySnapshotTracker> replicateInfoMap, Set<Integer> createIds) {
        outSequence = serverSequence;
        boolean hasBaseLine = maxSnapshotAck != 0;
        if (serverSequence - maxSnapshotAck > SnapshotConfig.SnapshotBufferSize) {
            hasBaseLine = false;
        }
        int baseLine = 0;
        if (hasBaseLine) {
            baseLine = maxSnapshotAck;
            sendAdditionSnapshot(output, baseLine, serverSequence, replicateInfoMap, createIds);
        } else {
            sendFullSnapshot(output, serverSequence, replicateInfoMap, createIds);
        }
        if (serverSequence > maxSnapshotAck) {
            maxSnapshotAck = serverSequence;
        }
    }

    //发送全量快照
    public void sendFullSnapshot(ByteBuf output, int serverSequence, Map<Integer, EntitySnapshotTracker> replicateInfoMap, Set<Integer> createIds) {
        SnapshotTools.resetByteBuf(output);
        List<byte[]> updateEntity = new ArrayList<>();
        for (EntitySnapshotTracker entityInfo : replicateInfoMap.values()) {
            if (entityInfo.getCreateSequence() == 0) {
                continue;
            }
            boolean destroyed = entityInfo.getDestroySequence() > 0;
            if (destroyed) {
                continue;
            }
            updateEntity.add(entityInfo.getSnapshot(output, serverSequence));
        }
        SnapshotTools.resetByteBuf(output);
        ReplicatedUtil.writeVarInt(output, updateEntity.size());
        for (byte[] info : updateEntity) {
            output.writeBytes(info);
        }
        byte[] updateBytes = SnapshotTools.byteBufToByteArray(output);
        sendFullSnapshot(inSequence, outSequence, updateBytes, createIds);
    }

    //发送增量快照
    private void sendAdditionSnapshot(ByteBuf output, int baseLine, int serverSequence, Map<Integer, EntitySnapshotTracker> replicateInfoMap, Set<Integer> createIds){
        List<Integer> destroyIds = new ArrayList<>();
        List<byte[]> updateEntity = new ArrayList<>();
        for (EntitySnapshotTracker entityInfo : replicateInfoMap.values()) {
            if (entityInfo.getCreateSequence() == 0) {
                continue;
            }
            boolean destroyed = entityInfo.getDestroySequence() > 0;
            if (destroyed && entityInfo.getDestroySequence() < baseLine) {
                continue;
            }
            int sendSequence = serverSequence;
            if (destroyed) {
                sendSequence = Math.max(entityInfo.getCreateSequence(), entityInfo.getDestroySequence());
            }
            byte[] additionSnapshot = entityInfo.generateAdditionSnapshot(output, baseLine, sendSequence);
            if (additionSnapshot != null) {
                updateEntity.add(additionSnapshot);
            }
            if (destroyed) {
                destroyIds.add(entityInfo.getId());
            }
        }

        SnapshotTools.resetByteBuf(output);
        ReplicatedUtil.writeVarInt(output, updateEntity.size());
        for (byte[] info : updateEntity) {
            output.writeBytes(info);
        }
        byte[] updateBytes = SnapshotTools.byteBufToByteArray(output);

        sendAdditionSnapshot(inSequence, outSequence, updateBytes, createIds, destroyIds);
    }

    /**
     * 向client发送增量快照数据
     * @param inSequence 该帧快照对应的客户端快照id
     * @param outSequence   该帧快照的服务器快照id
     * @param updateBytes   更新数据的实体id
     * @param createIds     outSequence帧创建的实体id
     * @param destroyIds    已经删除的实体id
     */
    protected abstract void sendAdditionSnapshot(int inSequence, int outSequence, byte[] updateBytes, Collection<Integer> createIds, Collection<Integer> destroyIds);

    /**
     * 向client发送全量快照数据
     * @param inSequence 该帧快照对应的客户端快照id
     * @param outSequence   该帧快照的服务器快照id
     * @param updateBytes   更新数据的实体id
     * @param createIds     outSequence帧创建的实体id
     */
    protected abstract void sendFullSnapshot(int inSequence, int outSequence, byte[] updateBytes, Collection<Integer> createIds);

    public int getMaxSnapshotAck() {
        return maxSnapshotAck;
    }

    public void setMaxSnapshotAck(int maxSnapshotAck) {
        this.maxSnapshotAck = maxSnapshotAck;
    }

    public void deserializer(byte[] byteArray) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteArray);
        int size = ReplicatedUtil.readVarInt(byteBuf);
        ReplicatedReader reader = ReplicatedReader.getInstance(byteBuf);
        for (int i = 0; i < size; i++) {
            DeserializeEntity entity = deserializeFactory.deserialize(reader);
            if (null == entity) {
                logger.error("ConnectionId:{} deserialize entity failed! inSequence:{} outSequence:{} dataIndex:{}.",
                        uid, inSequence, outSequence, i);
                return;
            }
            receive(entity);
        }
    }

    protected abstract void receive(DeserializeEntity deserializeEntity);
}

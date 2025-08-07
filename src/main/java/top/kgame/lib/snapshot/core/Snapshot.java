package top.kgame.lib.snapshot.core;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.kgame.lib.snapshot.tools.ReplicatedUtil;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;
import top.kgame.lib.snapshot.tools.SnapshotTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class Snapshot {
    private static final Logger logger = LogManager.getLogger(Snapshot.class);
    private final int id;
    private final int type;
    private final byte[] head;
    Map<Integer, byte[]> componentData = new TreeMap<>();

    public Snapshot(int id, int type) {
        this.id = id;
        this.type = type;
        ReplicatedWriter writer = ReplicatedWriter.getInstance();
        writer.writeInteger(id);
        writer.writeInteger(type);
        head = writer.toBytes();
        writer.reset();
    }

    byte[] getFullSnapshot() {
        ByteBuf byteBuf = SnapshotTools.getByteBuf(SnapshotTools.BYTE_BUF_SIZE_MIDDLE);
        byteBuf.writeBytes(head);
        ReplicatedUtil.writeVarInt(byteBuf, componentData.size());
        for (byte[] data : componentData.values()) {
            if (data == null) {
                logger.error("getFullSnapshot failed! reason: componentData values has null");
                continue;
            }
            ReplicatedUtil.writeVarInt(byteBuf, data.length);
            byteBuf.writeBytes(data);
        }
        byte[] result = SnapshotTools.byteBufToByteArray(byteBuf);
        SnapshotTools.resetByteBuf(byteBuf);
        return result;
    }

    boolean registerComponentData(ComponentSerializer componentSerializer) {
        if (componentSerializer == null) {
            logger.error("Snapshot addComponentData failed! reason: SerializeAbleEntity[{}]'s component[TypeIndex:{}] componentSerializer is null", id, type);
            return false;
        }
        componentData.put(componentSerializer.getTypeId(), componentSerializer.serialize());
        return true;
    }

    public boolean isSame(Snapshot other) {
        if (other == null) {
            return false;
        }
        byte[][] selfData = componentData.values().toArray(new byte[0][]);
        byte[][] otherData = other.componentData.values().toArray(new byte[0][]);
        if (selfData.length != otherData.length) {
            return false;
        }
        for (int i = 0; i < selfData.length; i++) {
            boolean match = SnapshotTools.compareByteSame(selfData[i], otherData[i]);
            if (!match) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成增量快照数据
     * @param preSnapshot 用于对比的基准快照。
     * @return  增量快照的完整byte数据。包括id type size等字段。 如果与基准快照完全一致则返回null
     */
    byte[] generateAdditionData(Snapshot preSnapshot) {
        if (preSnapshot == null) {
            return getFullSnapshot();
        }
        List<byte[]> additionData = new ArrayList<>();
        for (int typeId : getComponentTypes()) {
            byte[] selfData = getComponentData(typeId);
            byte[] otherData = preSnapshot.getComponentData(typeId);
            if (null == otherData || !SnapshotTools.compareByteSame(selfData, otherData)) {
                additionData.add(selfData);
            }
        }
        if (additionData.isEmpty()) {
            return null;
        }

        ByteBuf byteBuf = SnapshotTools.getByteBuf(SnapshotTools.BYTE_BUF_SIZE_SMALL);
        byteBuf.writeBytes(head);
        ReplicatedUtil.writeVarInt(byteBuf,additionData.size());
        for (byte[] data : additionData) {
            if (data == null) {
                logger.error("generate AdditionData failed! reason: componentData values has null");
                continue;
            }
            ReplicatedUtil.writeVarInt(byteBuf, data.length);
            byteBuf.writeBytes(data);
        }
        byte[] result = SnapshotTools.byteBufToByteArray(byteBuf);
        SnapshotTools.resetByteBuf(byteBuf);
        return result;
    }

    private byte[] getComponentData(int componentTypeIndex) {
        return componentData.get(componentTypeIndex);
    }

    private Set<Integer> getComponentTypes() {
        return componentData.keySet();
    }
}

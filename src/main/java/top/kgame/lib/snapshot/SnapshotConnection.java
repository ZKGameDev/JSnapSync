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
    private final SnapshotServer snapshotServer;
    private final long uid;

    private int inSequence;
    private int outSequence;
    private int lastSnapshotAck;

    public SnapshotConnection(long uid, DeserializeFactory deserializeFactory, SnapshotServer snapshotServer) {
        this.uid = uid;
        this.deserializeFactory = deserializeFactory;
        this.snapshotServer = snapshotServer;
    }

    /**
     * 根据参数提供的序列号发送快照数据。如果上次ack的序列号与目标序列号插值小于buffer大小则发送差异快照，否则发送全量快照
     * @param targetSequence 目标序列号
     */
    public void sendPackage(int targetSequence) {
        if (targetSequence > snapshotServer.getSequence()) {
            throw new IllegalArgumentException("targetSequence > snapshotServer.getSequence. invalid targetSequence: " + targetSequence);
        }
        outSequence = targetSequence;
        int baseLine = lastSnapshotAck;
        if (targetSequence - lastSnapshotAck > SnapshotConfig.SnapshotBufferSize) {
            baseLine = Integer.MIN_VALUE;
        }
        if (baseLine > 0) {
            sendAdditionSnapshot(baseLine, targetSequence);
        } else {
            sendFullSnapshot(targetSequence);
        }
        if (targetSequence > lastSnapshotAck) {
            lastSnapshotAck = targetSequence;
        }
    }

    //发送全量快照
    private void sendFullSnapshot(int serverSequence) {
        List<byte[]> updateEntity = new ArrayList<>();
        for (EntitySnapshotTracker entityInfo : snapshotServer.getAllReplicateEntity()) {
            if (entityInfo.getCreateSequence() == 0) {
                continue;
            }
            boolean destroyed = entityInfo.getDestroySequence() > 0;
            if (destroyed) {
                continue;
            }
            updateEntity.add(entityInfo.getSnapshot(serverSequence));
        }

        ByteBuf byteBuf = SnapshotTools.getByteBuf(SnapshotTools.BYTE_BUF_SIZE_LARGE);
        ReplicatedUtil.writeVarInt(byteBuf, updateEntity.size());
        for (byte[] info : updateEntity) {
            byteBuf.writeBytes(info);
        }
        byte[] updateBytes = SnapshotTools.byteBufToByteArray(byteBuf);
        sendFullSnapshot(inSequence, outSequence, updateBytes, snapshotServer.getCreateReplicateId());
    }

    //发送增量快照
    private void sendAdditionSnapshot(int baseLine, int serverSequence){
        List<Integer> destroyIds = new ArrayList<>();
        List<byte[]> updateEntity = new ArrayList<>();
        for (EntitySnapshotTracker entityInfo : snapshotServer.getAllReplicateEntity()) {
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
            byte[] additionSnapshot = entityInfo.generateAdditionSnapshot(baseLine, sendSequence);
            if (additionSnapshot != null) {
                updateEntity.add(additionSnapshot);
            }
            if (destroyed) {
                destroyIds.add(entityInfo.getId());
            }
        }

        ByteBuf byteBuf = SnapshotTools.getByteBuf(SnapshotTools.BYTE_BUF_SIZE_BIG);
        ReplicatedUtil.writeVarInt(byteBuf, updateEntity.size());
        for (byte[] info : updateEntity) {
            byteBuf.writeBytes(info);
        }
        byte[] updateBytes = SnapshotTools.byteBufToByteArray(byteBuf);

        sendAdditionSnapshot(inSequence, outSequence, updateBytes, snapshotServer.getCreateReplicateId(), destroyIds);
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

    public int getLastSnapshotAck() {
        return lastSnapshotAck;
    }

    /**
     * 反序列化输入数据
     * @param inSequence 输入数据序列号
     * @param byteArray 二进制数据
     */
    public void deserializer(int inSequence, byte[] byteArray) {
        this.inSequence = inSequence;
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
            receive(inSequence, entity);
        }
    }

    protected abstract void receive(int inSequence, DeserializeEntity deserializeEntity);
}
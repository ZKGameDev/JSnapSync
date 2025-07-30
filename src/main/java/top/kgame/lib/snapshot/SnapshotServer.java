package top.kgame.lib.snapshot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import top.kgame.lib.snapshot.core.EntitySnapshotTracker;
import top.kgame.lib.snapshot.tools.SnapshotTools;

import java.util.*;

public abstract class SnapshotServer {
    private final ByteBuf output = Unpooled.buffer(10240);
    private int serverSequence = 1;
    private final Set<Integer> createIds = new HashSet<>();
    private final Map<Integer, EntitySnapshotTracker> replicateInfoMap = new TreeMap<>();

    protected abstract Collection<SnapshotConnection> getAllConnection();

    public void registerEntity(SerializeEntity entity) {
        EntitySnapshotTracker replicateInfo = EntitySnapshotTracker.generate(entity);
        replicateInfo.setCreateSequence(serverSequence);
        replicateInfoMap.put(entity.getGuid(), replicateInfo);
        createIds.add(entity.getGuid());
    }

    public SerializeEntity unregisterEntity(int replicateId) {
        EntitySnapshotTracker replicateInfo = replicateInfoMap.get(replicateId);
        if (null == replicateInfo) {
            return null;
        }
        replicateInfo.setDestroySequence(serverSequence);
        return replicateInfo.getEntity();
    }

    public void unregisterEntity(SerializeEntity entity) {
        EntitySnapshotTracker replicateInfo = replicateInfoMap.get(entity.getGuid());
        if (null == replicateInfo) {
            return;
        }
        replicateInfo.setDestroySequence(serverSequence);
    }

    /**
     * 执行一个快照同步步骤
     * 包含生成快照、广播到所有连接、递增服务器序列号三个操作
     */
    public void stepSnapshot() {
        generateSnapshot();
        broadcastSnapshot();
        this.serverSequence++;
        createIds.clear();
    }

    private void generateSnapshot() {
        int minClientAck = calMinClientAck();
        ArrayList<Integer> needRemoveReplicateIds = new ArrayList<>();
        for (EntitySnapshotTracker info : replicateInfoMap.values()) {
            //移除已经所有客户端已经收到的已销毁的entity
            if (info.getDestroySequence() > 0 && minClientAck > info.getDestroySequence()) {
                needRemoveReplicateIds.add(info.getId());
                continue;
            }
            if (info.getDestroySequence() > 0 && this.serverSequence > info.getDestroySequence()) {
                continue;
            }
            info.generateEntitySnapshot(serverSequence);
            int preSequence = serverSequence - 1;
            if (info.hasSnapshot(preSequence)) {
                if (!info.compareSnapshotSame(preSequence, serverSequence)) {
                    info.setUpdateSequence(serverSequence);
                }
            } else {
                info.setUpdateSequence(serverSequence);
            }
        }
        for (Integer needRemoveId : needRemoveReplicateIds) {
            replicateInfoMap.remove(needRemoveId);
        }
    }

    private int calMinClientAck() {
        int minClientAck = Integer.MAX_VALUE;
        for (SnapshotConnection connection : getAllConnection()) {
            int ackedSequence = connection.getMaxSnapshotAck();
            if (ackedSequence < minClientAck) {
                minClientAck = ackedSequence;
            }
        }
        return minClientAck;
    }

    private void broadcastSnapshot() {
        for (SnapshotConnection connection : getAllConnection()) {
            SnapshotTools.resetByteBuf(output);
            connection.sendPackage(output, serverSequence, replicateInfoMap, createIds);
        }
    }
}

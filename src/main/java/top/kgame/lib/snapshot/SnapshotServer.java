package top.kgame.lib.snapshot;

import top.kgame.lib.snapshot.core.EntitySnapshotTracker;

import java.util.*;

public abstract class SnapshotServer {
    private int serverSequence = 1;
    private final Set<Integer> createIds = new HashSet<>();
    private final Map<Integer, EntitySnapshotTracker> replicateInfoMap = new TreeMap<>();
    private final List<SnapshotConnection> connections = new ArrayList<>();

    private Collection<SnapshotConnection> getAllConnection() {
        return connections;
    }

    /**
     * 注册一个客户端连接
     * @param connection 客户端连接对象
     */
    public void registerConnection(final SnapshotConnection connection) {
        if (null == connection) {
            return;
        }
        connections.add(connection);
    }

    /**
     * 注册一个需要同步的实体
     * @param entity 要注册的实体对象
     */
    public void registerEntity(SerializeEntity entity) {
        EntitySnapshotTracker replicateInfo = EntitySnapshotTracker.generate(entity);
        replicateInfo.setCreateSequence(serverSequence);
        replicateInfoMap.put(entity.getGuid(), replicateInfo);
        createIds.add(entity.getGuid());
    }

    /**
     * 根据ID注销一个实体
     * @param replicateId 实体的复制ID
     * @return 被注销的实体对象，如果不存在则返回null
     */
    public SerializeEntity unregisterEntity(int replicateId) {
        EntitySnapshotTracker replicateInfo = replicateInfoMap.get(replicateId);
        if (null == replicateInfo) {
            return null;
        }
        replicateInfo.setDestroySequence(serverSequence);
        return replicateInfo.getEntity();
    }

    /**
     * 注销指定的实体对象
     * @param entity 要注销的实体对象
     */
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
            int ackedSequence = connection.getLastSnapshotAck();
            if (ackedSequence < minClientAck) {
                minClientAck = ackedSequence;
            }
        }
        return minClientAck;
    }

    private void broadcastSnapshot() {
        for (SnapshotConnection connection : getAllConnection()) {
            connection.sendPackage(serverSequence);
        }
    }

    /**
     * 获取当前服务器序列号
     * @return 当前服务器序列号
     */
    public int getSequence() {
        return serverSequence;
    }

    /**
     * 获取所有已注册的实体快照跟踪器
     * @return 实体快照跟踪器集合
     */
    public Collection<EntitySnapshotTracker> getAllReplicateEntity() {
        return replicateInfoMap.values();
    }

    /**
     * 获取本次创建的实体ID集合
     * @return 创建的实体ID集合
     */
    public Collection<Integer> getCreateReplicateId() {
        return createIds;
    }
}

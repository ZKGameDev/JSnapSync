package top.kgame.lib.snapshot;

import top.kgame.lib.snapshot.tools.ReplicatedReader;

import java.util.Collection;

/**
 * 可序列化实体，该实体可拥有多个可序列化组件
 */
public interface DeserializeEntity {
    void deserialize(ReplicatedReader reader);
}

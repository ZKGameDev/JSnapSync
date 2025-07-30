package top.kgame.lib.snapshot;

import java.util.Collection;

/**
 * 可序列化实体，该实体可拥有多个可序列化组件
 */
public interface SerializeEntity {
    Collection<SerializeComponent> getComponents();
    int getGuid();
    int getTypeId();
}

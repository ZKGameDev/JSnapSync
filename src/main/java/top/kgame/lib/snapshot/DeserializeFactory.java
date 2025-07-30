package top.kgame.lib.snapshot;

import top.kgame.lib.snapshot.tools.ReplicatedReader;

public abstract class DeserializeFactory {
    public DeserializeEntity deserialize(ReplicatedReader reader) {
        int entityId = reader.readInteger();
        int entityType = reader.readInteger();
        DeserializeEntity deserializeEntity = createEntity(entityId, entityType);
        if (null == deserializeEntity) {
            return null;
        }
        deserializeEntity.deserialize(reader);
        return deserializeEntity;
    }
    protected abstract DeserializeEntity createEntity(int id, int type);
}
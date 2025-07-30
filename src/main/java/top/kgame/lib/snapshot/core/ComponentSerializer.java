package top.kgame.lib.snapshot.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.kgame.lib.snapshot.SerializeComponent;
import top.kgame.lib.snapshot.SerializeEntity;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;

public class ComponentSerializer {
    private static final Logger logger = LogManager.getLogger(ComponentSerializer.class);
    private final SerializeEntity serializeEntity;
    private final SerializeComponent serializer;
    private ComponentSerializer(SerializeEntity serializeEntity, SerializeComponent serializer) {
        this.serializeEntity = serializeEntity;
        this.serializer = serializer;
    }

    public byte[] serialize() {
        ReplicatedWriter writer = ReplicatedWriter.getInstance();
        serializer.serialize(writer);
        writer.reset();
        return writer.toBytes();
    }

    public SerializeEntity getSerializeEntity() {
        return serializeEntity;
    }

    public SerializeComponent getSerializer() {
        return serializer;
    }

    public static ComponentSerializer generate(SerializeEntity entity, SerializeComponent component) {
        return new ComponentSerializer(entity, component);
    }

    public Integer getTypeId() {
        return serializer.getTypeId();
    }

    @Override
    public String toString() {
        return "ReplicatedComponent{" +
                "serializeEntityGuid=" + serializeEntity.getGuid() +
                "serializeEntityTypeId=" + serializeEntity.getTypeId() +
                ", serializerTypeId=" + serializer.getTypeId() +
                '}';
    }
}

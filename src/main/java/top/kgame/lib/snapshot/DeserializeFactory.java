package top.kgame.lib.snapshot;

import top.kgame.lib.snapshot.tools.ReplicatedReader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 反序列化工厂类，负责根据类型ID创建和反序列化实体对象
 */
public class DeserializeFactory {
    
    /**
     * 类型ID到实体创建器的映射
     */
    private final Map<Integer, Supplier<? extends DeserializeEntity>> entitySuppliers = new HashMap<>();

    /**
     * 反序列化实体
     * @param reader 数据读取器
     * @return 反序列化后的实体对象，如果类型未注册则返回null
     */
    public DeserializeEntity deserialize(ReplicatedReader reader) {
        int entityId = reader.readInteger();
        int entityType = reader.readInteger();
        
        Supplier<? extends DeserializeEntity> supplier = entitySuppliers.get(entityType);
        if (supplier == null) {
            return null;
        }

        DeserializeEntity deserializeEntity = supplier.get();
        deserializeEntity.setGuid(entityId);
        deserializeEntity.setTypeId(entityType);
        deserializeEntity.deserialize(reader);
        
        return deserializeEntity;
    }

    /**
     * 注册实体类型
     * @param type 实体类型ID
     * @param supplier 实体创建器
     */
    public void registerEntityType(int type, Supplier<? extends DeserializeEntity> supplier) {
        entitySuppliers.put(type, supplier);
    }
    
    /**
     * 注册实体类型（使用类引用）
     * @param type 实体类型ID
     * @param entityClass 实体类
     * @param <T> 实体类型
     */
    public <T extends DeserializeEntity> void registerEntityType(int type, Class<T> entityClass) {
        entitySuppliers.put(type, () -> {
            try {
                return entityClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + entityClass.getName(), e);
            }
        });
    }
    
    /**
     * 检查是否已注册指定类型
     * @param type 实体类型ID
     * @return 是否已注册
     */
    public boolean isRegistered(int type) {
        return entitySuppliers.containsKey(type);
    }
    
    /**
     * 获取已注册的类型数量
     * @return 注册的类型数量
     */
    public int getRegisteredTypeCount() {
        return entitySuppliers.size();
    }
}
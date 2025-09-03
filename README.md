# JSnapSync

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)

JSnapSync 是一个专为游戏服务器设计的Java快照同步库，提供高效的实体状态同步解决方案。它支持组件级别的增量快照同步，能够在保证数据一致性的同时显著减少网络传输开销。

## 🚀 核心特性

- **增量同步**：仅同步发生变化的组件数据，大幅减少网络带宽使用
- **组件架构**：基于实体-组件模式，支持灵活的数据结构设计
- **高性能序列化**：使用优化的二进制序列化协议，支持多种数据类型
- **缓冲机制**：内置快照缓冲区，支持历史状态追溯和丢包重传
- **连接管理**：支持多客户端连接和个性化快照发送
- **类型安全**：完整的类型系统支持，包括基础类型、数组、集合和自定义对象

## 📋 系统要求

- Java 21 或更高版本
- Maven 3.6 或更高版本


## 🏗️ 架构概览

JSnapSync 采用以下核心架构：

### 同步实体层次结构
```
同步实体 (SerializeEntity)
├── 同步组件1 (SerializeComponent)
│   ├── 字段1
│   ├── 字段2
│   └── ...
├── 同步组件2 (SerializeComponent)
└── 同步组件3 (SerializeComponent)
```

### 核心组件

- **SnapshotServer**：快照服务器，管理实体注册和快照生成
- **SnapshotConnection**：客户端连接，处理个性化快照发送
- **EntitySnapshotTracker**：实体快照跟踪器，管理单个实体的快照历史
- **SnapshotBuffer**：快照缓冲区，存储历史快照数据
- **ComponentSerializer**：组件序列化器，处理组件级别的序列化

## 🔧 快速开始

### 1. 定义同步组件

```java
public class PlayerComponent implements SerializeComponent, DeserializeComponent {
    private String name;
    private int level;
    private float health;
    
    @Override
    public Integer getTypeId() {
        return 1; // 组件类型ID
    }
    
    @Override
    public void serialize(ReplicatedWriter writer) {
        writer.writeString(name);
        writer.writeInteger(level);
        writer.writeFloat(health);
    }
    
    @Override
    public void deserialize(ReplicatedReader reader) {
        name = reader.readString();
        level = reader.readInteger();
        health = reader.readFloat();
    }
    
    // getters and setters...
}
```

### 2. 定义同步实体

```java
public class Player implements SerializeEntity, DeserializeEntity {
    private final int guid;
    private final List<SerializeComponent> components = new ArrayList<>();
    
    public Player(int guid) {
        this.guid = guid;
        // 添加组件
        components.add(new PlayerComponent());
    }
    
    @Override
    public Collection<SerializeComponent> getComponents() {
        return components;
    }
    
    @Override
    public int getGuid() {
        return guid;
    }
    
    @Override
    public int getTypeId() {
        return 100; // 实体类型ID
    }
    
    @Override
    public void deserialize(ReplicatedReader reader) {
        // 反序列化逻辑
    }
}
```

### 3. 创建快照服务器

```java
public class GameSnapshotServer extends SnapshotServer {
    
    public void initializeGame() {
        // 注册实体类型到反序列化工厂
        getDeserializeFactory().registerEntityType(100, () -> new Player(0));
        getDeserializeFactory().registerEntityType(101, () -> new Monster(0));
        
        // 注册玩家实体
        Player player = new Player(1001);
        registerEntity(player);
        
        // 注册客户端连接
        GameConnection connection = new GameConnection(userId, this);
        registerConnection(connection);
    }
    
    public void gameLoop() {
        // 在游戏循环中执行快照同步
        stepSnapshot();
    }
}
```

### 4. 实现客户端连接

```java
public class GameConnection extends SnapshotConnection {
    
    public GameConnection(long uid, DeserializeFactory factory, SnapshotServer server) {
        super(uid, factory, server);
    }
    
    @Override
    protected void sendSnapshot(byte[] data) {
        // 将快照数据发送给客户端
        // 这里实现具体的网络发送逻辑
    }
    
    @Override
    public void receiveAck(int sequence) {
        // 处理客户端的确认消息
        ackSnapshot(sequence);
    }
}
```

## 📊 支持的数据类型

JSnapSync 支持丰富的数据类型序列化：

### 基础类型
- `byte`, `char`, `boolean`, `short`, `int`, `long`, `float`, `double`
- `String`, `byte[]`

### 集合类型
- `List<T>` (支持所有基础类型的List)
- 基础类型数组 (`int[]`, `String[]`, 等)

### 自定义对象
- 实现 `SerializeStruct` 接口的自定义结构体
- 支持嵌套对象和对象集合

## ⚙️ 高级特性

### 增量快照
系统自动检测组件变化，仅发送修改过的数据：
```java
// 如果玩家血量改变，只同步PlayerComponent
player.getComponent(PlayerComponent.class).setHealth(80.0f);
// 下次快照只会包含PlayerComponent的变更数据
```

### 快照缓冲
内置缓冲机制支持：
- 历史快照查询
- 丢包重传
- 差异计算

### 序列号管理
- 服务器维护全局序列号
- 客户端确认机制
- 自动清理过期快照

## 🧪 测试

运行测试套件：
```bash
mvn test
```

测试包含：
- 组件序列化/反序列化测试
- 边界值测试
- 特殊字符和Unicode支持测试
- 增量快照功能测试

## 📝 注意事项

1. **组件固定性**：实体注册后不支持动态增删组件
2. **类型ID唯一性**：确保组件和实体的类型ID在系统中唯一
3. **序列化顺序**：serialize和deserialize方法中的字段顺序必须严格对应
4. **内存管理**：大型游戏建议定期清理过期快照缓冲
5. **实体类型注册**：使用Supplier接口注册实体类型，避免反射调用开销

## 🔧 实体类型注册

JSnapSync 使用 Supplier 接口进行实体类型注册，提供多种注册方式：

### 方式1：Lambda表达式
```java
factory.registerEntityType(100, () -> new Player(0));
```

### 方式2：方法引用（需要无参构造函数）
```java
factory.registerEntityType(101, Player::new);
```

### 方式3：类引用（需要无参构造函数）
```java
factory.registerEntityType(102, Player.class);
```

### 方式4：匿名内部类
```java
factory.registerEntityType(103, new Supplier<Player>() {
    @Override
    public Player get() {
        return new Player(0);
    }
});
```

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详细信息请查看 [LICENSE](LICENSE) 文件。

## 🔗 相关链接

- [项目主页](https://github.com/ZKGameDev/JSnapSync)
- [问题反馈](https://github.com/ZKGameDev/JSnapSync/issues)
- [KGame生态系统](https://github.com/ZKGameDev)

---

*JSnapSync - 让游戏服务器状态同步变得简单高效* 🎮
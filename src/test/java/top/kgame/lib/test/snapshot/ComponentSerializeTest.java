package top.kgame.lib.test.snapshot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import top.kgame.lib.snapshot.tools.ReplicatedReader;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;
import top.kgame.lib.test.snapshot.struct.TestSyncComponent;
import top.kgame.lib.test.snapshot.struct.TestSyncStruct;

public class ComponentSerializeTest {
    @Test
    public void test() {
        TestSyncComponent encodeComponent = new TestSyncComponent();
        
        // 设置所有基本类型变量
        encodeComponent.setB((byte) 2);
        encodeComponent.setC('A');
        encodeComponent.setBl(true);
        encodeComponent.setInt16((short) 1234);
        encodeComponent.setInt32(567890);
        encodeComponent.setInt64(9876543210L);
        encodeComponent.setF32(3.14f);
        encodeComponent.setF64(2.718281828);
        encodeComponent.setBs(new byte[]{1, 2, 3, 4, 5});
        encodeComponent.setSs("测试字符串");
        
        // 创建并设置嵌套的TestSyncStruct对象
        TestSyncStruct encodeStruct = new TestSyncStruct();
        encodeStruct.setB((byte) 10);
        encodeStruct.setC('B');
        encodeStruct.setBl(false);
        encodeStruct.setInt16((short) 5678);
        encodeStruct.setInt32(123456);
        encodeStruct.setInt64(1122334455L);
        encodeStruct.setF32(1.618f);
        encodeStruct.setF64(1.4142135623);
        encodeStruct.setBs(new byte[]{6, 7, 8, 9, 10});
        encodeStruct.setSs("嵌套结构测试");
        
        encodeComponent.setStruct(encodeStruct);

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
    }

    @Test
    public void test1() {
        TestSyncComponent encodeComponent = new TestSyncComponent();
        // 设置所有基本类型变量
        encodeComponent.setB(Byte.MIN_VALUE);
        encodeComponent.setC('？');
        encodeComponent.setBl(false);
        encodeComponent.setInt16(Short.MIN_VALUE);
        encodeComponent.setInt32(Integer.MIN_VALUE);
        encodeComponent.setInt64(Long.MIN_VALUE);
        encodeComponent.setF32(Float.MIN_VALUE);
        encodeComponent.setF64(Double.MIN_VALUE);
        encodeComponent.setBs(null);
        encodeComponent.setSs("null");

        encodeComponent.setStruct(null);

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
    }

    @Test
    public void test3() {
        TestSyncComponent encodeComponent = new TestSyncComponent();

        // 设置所有基本类型变量
        encodeComponent.setB(Byte.MAX_VALUE);
        encodeComponent.setC('A');
        encodeComponent.setBl(true);
        encodeComponent.setInt16(Short.MAX_VALUE);
        encodeComponent.setInt32(Integer.MAX_VALUE);
        encodeComponent.setInt64(Long.MIN_VALUE);
        encodeComponent.setF32(Float.MAX_VALUE);
        encodeComponent.setF64(Double.MAX_VALUE);
        encodeComponent.setBs(new byte[]{1, 2, 3, 4, 5});
        encodeComponent.setSs("测试字符串");

        // 创建并设置嵌套的TestSyncStruct对象
        TestSyncStruct encodeStruct = new TestSyncStruct();
        encodeStruct.setB((byte) 0);
        encodeStruct.setC('B');
        encodeStruct.setBl(false);
        encodeStruct.setInt16((short) 0);
        encodeStruct.setInt32(0);
        encodeStruct.setInt64(0);
        encodeStruct.setF32(0f);
        encodeStruct.setF64(0);
        encodeStruct.setBs(new byte[]{6, 7, 8, 9, 10});
        encodeStruct.setSs("嵌套结构测试");

        encodeComponent.setStruct(encodeStruct);

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
    }
}
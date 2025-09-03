package top.kgame.lib.test.snapshot;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import top.kgame.lib.snapshot.tools.ReplicatedReader;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;
import top.kgame.lib.test.snapshot.struct.TestSyncComponent;
import top.kgame.lib.test.snapshot.struct.TestSyncStruct;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import top.kgame.lib.snapshot.DeserializeFactory;
import top.kgame.lib.test.snapshot.struct.TestSyncEntity;

public class ComponentSerializeTest {
    @Test
    public void testNormalValues() {
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
        
        // 设置List类型
        encodeComponent.setListObj(Arrays.asList(encodeStruct));
        encodeComponent.setListBoolean(Arrays.asList(true, false, true));
        encodeComponent.setListByte(Arrays.asList((byte) 1, (byte) 2, (byte) 3));
        encodeComponent.setListChar(Arrays.asList('A', 'B', '中'));
        encodeComponent.setListShort(Arrays.asList((short) 100, (short) 200));
        encodeComponent.setListInt(Arrays.asList(1000, 2000, 3000));
        encodeComponent.setListLong(Arrays.asList(10000L, 20000L));
        encodeComponent.setListFloat(Arrays.asList(1.1f, 2.2f, 3.3f));
        encodeComponent.setListDouble(Arrays.asList(1.11, 2.22, 3.33));
        encodeComponent.setListString(Arrays.asList("字符串1", "字符串2", "字符串3"));
        
        // 设置数组类型
        encodeComponent.setBooleanArray(new boolean[]{true, false, true, false});
        encodeComponent.setCharArray(new char[]{'X', 'Y', 'Z', '测'});
        encodeComponent.setShortArray(new short[]{300, 400, 500});
        encodeComponent.setIntArray(new int[]{4000, 5000, 6000});
        encodeComponent.setLongArray(new long[]{40000L, 50000L, 60000L});
        encodeComponent.setFloatArray(new float[]{4.4f, 5.5f, 6.6f});
        encodeComponent.setDoubleArray(new double[]{4.44, 5.55, 6.66});
        encodeComponent.setStringArray(new String[]{"数组1", "数组2", "数组3"});

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
        replicatedWriter.reset();
        
        System.out.println("testNormalValues 测试通过！");
    }

    @Test
    public void testMinValuesAndNulls() {
        TestSyncComponent encodeComponent = new TestSyncComponent();
        
        // 设置所有基本类型变量为最小值
        encodeComponent.setB(Byte.MIN_VALUE);
        encodeComponent.setC('\u0000');
        encodeComponent.setBl(false);
        encodeComponent.setInt16(Short.MIN_VALUE);
        encodeComponent.setInt32(Integer.MIN_VALUE);
        encodeComponent.setInt64(Long.MIN_VALUE);
        encodeComponent.setF32(Float.MIN_VALUE);
        encodeComponent.setF64(Double.MIN_VALUE);
        encodeComponent.setBs(null);
        encodeComponent.setSs(null);
        encodeComponent.setStruct(null);

        // 设置List类型为null或空
        encodeComponent.setListObj(null);
        encodeComponent.setListBoolean(new ArrayList<>());
        encodeComponent.setListByte(null);
        encodeComponent.setListChar(new ArrayList<>());
        encodeComponent.setListShort(null);
        encodeComponent.setListInt(new ArrayList<>());
        encodeComponent.setListLong(null);
        encodeComponent.setListFloat(new ArrayList<>());
        encodeComponent.setListDouble(null);
        encodeComponent.setListString(Arrays.asList(null, "", null));
        
        // 设置数组类型为null或空
        encodeComponent.setBooleanArray(null);
        encodeComponent.setCharArray(new char[0]);
        encodeComponent.setShortArray(null);
        encodeComponent.setIntArray(new int[0]);
        encodeComponent.setLongArray(null);
        encodeComponent.setFloatArray(new float[0]);
        encodeComponent.setDoubleArray(null);
        encodeComponent.setStringArray(new String[]{null, null});

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
        replicatedWriter.reset();
        
        System.out.println("testMinValuesAndNulls 测试通过！");
    }

    @Test
    public void testMaxValuesAndSpecialCases() {
        TestSyncComponent encodeComponent = new TestSyncComponent();

        // 设置所有基本类型变量为最大值和特殊值
        encodeComponent.setB(Byte.MAX_VALUE);
        encodeComponent.setC('\uFFFF');
        encodeComponent.setBl(true);
        encodeComponent.setInt16(Short.MAX_VALUE);
        encodeComponent.setInt32(Integer.MAX_VALUE);
        encodeComponent.setInt64(Long.MAX_VALUE);
        encodeComponent.setF32(Float.MAX_VALUE);
        encodeComponent.setF64(Double.MAX_VALUE);
        encodeComponent.setBs(new byte[]{Byte.MIN_VALUE, Byte.MAX_VALUE, 0});
        encodeComponent.setSs("🌟特殊字符测试🚀\n\t换行制表符");

        // 创建并设置嵌套的TestSyncStruct对象
        TestSyncStruct encodeStruct = new TestSyncStruct();
        encodeStruct.setB((byte) 0);
        encodeStruct.setC('中');
        encodeStruct.setBl(true);
        encodeStruct.setInt16((short) 0);
        encodeStruct.setInt32(0);
        encodeStruct.setInt64(0);
        encodeStruct.setF32(Float.NaN);
        encodeStruct.setF64(Double.POSITIVE_INFINITY);
        encodeStruct.setBs(new byte[0]);
        encodeStruct.setSs("");

        encodeComponent.setStruct(encodeStruct);
        
        // 设置List类型为包含各种特殊值
        TestSyncStruct struct2 = new TestSyncStruct();
        struct2.setB((byte) -1);
        struct2.setC('Ω');
        struct2.setBl(false);
        struct2.setInt16((short) -32768);
        struct2.setInt32(-1);
        struct2.setInt64(-1L);
        struct2.setF32(Float.NEGATIVE_INFINITY);
        struct2.setF64(Double.NaN);
        struct2.setBs(new byte[]{-128, 127});
        struct2.setSs("特殊struct");
        
        encodeComponent.setListObj(Arrays.asList(encodeStruct, struct2, null));
        encodeComponent.setListBoolean(Arrays.asList(true, true, false, false, true));
        encodeComponent.setListByte(Arrays.asList(Byte.MIN_VALUE, (byte) 0, Byte.MAX_VALUE));
        encodeComponent.setListChar(Arrays.asList('\u0000', 'A', '\uFFFF', '中'));
        encodeComponent.setListShort(Arrays.asList(Short.MIN_VALUE, (short) 0, Short.MAX_VALUE));
        encodeComponent.setListInt(Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
        encodeComponent.setListLong(Arrays.asList(Long.MIN_VALUE, 0L, Long.MAX_VALUE));
        encodeComponent.setListFloat(Arrays.asList(Float.MIN_VALUE, 0.0f, Float.MAX_VALUE, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
        encodeComponent.setListDouble(Arrays.asList(Double.MIN_VALUE, 0.0, Double.MAX_VALUE, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        encodeComponent.setListString(Arrays.asList("", "正常字符串", null, "🌟特殊符号🚀", "\n换行\t制表符"));
        
        // 设置数组类型为包含各种边界值
        encodeComponent.setBooleanArray(new boolean[]{true, false});
        encodeComponent.setCharArray(new char[]{'\u0000', 'Z', '\uFFFF'});
        encodeComponent.setShortArray(new short[]{Short.MIN_VALUE, 0, Short.MAX_VALUE});
        encodeComponent.setIntArray(new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE});
        encodeComponent.setLongArray(new long[]{Long.MIN_VALUE, Long.MAX_VALUE});
        encodeComponent.setFloatArray(new float[]{Float.MIN_VALUE, Float.MAX_VALUE, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY});
        encodeComponent.setDoubleArray(new double[]{Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY});
        encodeComponent.setStringArray(new String[]{"边界测试", "", null, "🎯Unicode测试🌟"});

        ReplicatedWriter replicatedWriter = ReplicatedWriter.getInstance();
        encodeComponent.serialize(replicatedWriter);
        byte[] encodeData = replicatedWriter.toBytes();

        ByteBuf decodeByteBuf = Unpooled.buffer();
        decodeByteBuf.writeBytes(encodeData);
        ReplicatedReader replicatedReader = ReplicatedReader.getInstance(decodeByteBuf);
        TestSyncComponent decodeComponent = new TestSyncComponent();
        decodeComponent.deserialize(replicatedReader);

        assert encodeComponent.equals(decodeComponent);
        replicatedWriter.reset();
        
        System.out.println("testMaxValuesAndSpecialCases 测试通过！");
    }
    
    /**
     * 演示如何使用新的Supplier接口注册实体类型
     */
    @Test
    public void testDeserializeFactoryWithSupplier() {
        DeserializeFactory factory = new DeserializeFactory();
        
        // 方式1：使用Lambda表达式
        factory.registerEntityType(100, () -> new TestSyncEntity(0, 100));
        
        // 方式2：使用方法引用（如果有无参构造函数）
        // factory.registerEntityType(101, TestSyncEntity::new);
        
        // 方式3：使用类引用（需要无参构造函数）
        // factory.registerEntityType(102, TestSyncEntity.class);
        
        // 方式4：使用匿名内部类
        factory.registerEntityType(103, new Supplier<TestSyncEntity>() {
            @Override
            public TestSyncEntity get() {
                return new TestSyncEntity(0, 103);
            }
        });
        
        // 验证注册
        assert factory.isRegistered(100);
        assert factory.isRegistered(103);
        assert !factory.isRegistered(999);
        assert factory.getRegisteredTypeCount() == 2;
        
        System.out.println("testDeserializeFactoryWithSupplier 测试通过！");
    }
}
package top.kgame.lib.snapshot.tools;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.kgame.lib.snapshot.DeserializeComponent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReplicatedReader {
    private static final Logger logger = LogManager.getLogger(ReplicatedReader.class);
    private final ByteBuf byteBuf;
    private ReplicatedReader(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public static ReplicatedReader getInstance(ByteBuf byteBuf) {
        return new ReplicatedReader(byteBuf);
    }

    public boolean readBoolean() {
        return byteBuf.readBoolean();
    }

    public List<Boolean> readBooleanList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readBoolean());
        }
        return result;
    }

    public boolean[] readBooleanArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new boolean[0];
        }
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            result[i] = readBoolean();
        }
        return result;
    }

    public byte readByte() {
        return byteBuf.readByte();
    }

    public List<Byte> readByteList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Byte> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readByte());
        }
        return result;
    }

    public byte[] readByteArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public char readChar() {
        return byteBuf.readChar();
    }

    public List<Character> readCharList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readChar());
        }
        return result;
    }

    public char[] readCharArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new char[0];
        }
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = readChar();
        }
        return result;
    }

    public short readShort() {
        return byteBuf.readShort();
    }

    public List<Short> readShortList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Short> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readShort());
        }
        return result;
    }

    public short[] readShortArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new short[0];
        }
        short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = readShort();
        }
        return result;
    }

    public int readInteger() {
        return ReplicatedUtil.readVarInt(byteBuf);
    }

    public List<Integer> readIntList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readInteger());
        }
        return result;
    }

    public int[] readIntArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new int[0];
        }
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = readInteger();
        }
        return result;
    }

    public long readLong() {
        return byteBuf.readLong();
    }

    public List<Long> readLongList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readLong());
        }
        return result;
    }

    public long[] readLongArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new long[0];
        }
        long[] result = new long[length];
        for (int i = 0; i < length; i++) {
            result[i] = readLong();
        }
        return result;
    }

    public float readFloat() {
        return byteBuf.readFloat();
    }

    public List<Float> readFloatList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readFloat());
        }
        return result;
    }

    public float[] readFloatArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new float[0];
        }
        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = readFloat();
        }
        return result;
    }

    public double readDouble() {
        return byteBuf.readDouble();
    }

    public List<Double> readDoubleList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readDouble());
        }
        return result;
    }

    public double[] readDoubleArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new double[0];
        }
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = readDouble();
        }
        return result;
    }

    public String readString() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (0 == length) {
            return "";
        }
        byte[] data = SnapshotTools.byteBufToByteArray(byteBuf.readBytes(length));
        return new String(data, StandardCharsets.UTF_8);
    }

    public List<String> readStringList() {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readString());
        }
        return result;
    }

    public String[] readStringArray() {
        int length = readInteger();
        if (length == -1) {
            return null;
        }
        if (length == 0) {
            return new String[0];
        }
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = readString();
        }
        return result;
    }

    public <T extends DeserializeComponent> T readObject(Class<T> tClass) {
        boolean isNull = readBoolean();
        if (isNull) {
            return null;
        }
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T obj = constructor.newInstance();
            obj.deserialize(this);
            return obj;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("类 " + tClass.getName() + " 没有默认构造函数", e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("无法实例化抽象类或接口: " + tClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("无法访问构造函数: " + tClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("构造函数抛出异常: " + tClass.getName(), e);
        }
    }

    public <T extends DeserializeComponent> List<T> readObjList(Class<T> tClass) {
        int size = readInteger();
        if (-1 == size) {
            return null;
        }
        if (0 == size) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readObject(tClass));
        }
        return result;
    }

}

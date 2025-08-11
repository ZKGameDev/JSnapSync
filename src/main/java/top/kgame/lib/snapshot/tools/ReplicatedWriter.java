package top.kgame.lib.snapshot.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import top.kgame.lib.snapshot.SerializeComponent;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReplicatedWriter {
    private final ByteBuf byteBuf;
    private final Handle<ReplicatedWriter> handle;
    ReplicatedWriter(Handle<ReplicatedWriter> handle) {
        this(512, handle);
    }
    ReplicatedWriter(int size, Handle<ReplicatedWriter> handle){
        byteBuf = Unpooled.buffer(size);
        this.handle = handle;
    }

    private static final Recycler<ReplicatedWriter> RECYCLER = new Recycler<ReplicatedWriter>() {
        @Override
        protected ReplicatedWriter newObject(Handle<ReplicatedWriter> handle) {
            return new ReplicatedWriter(handle); // 创建新实例时绑定Handle
        }
    };

    public static ReplicatedWriter getInstance() {
        return RECYCLER.get();
    }

    public void writeBoolean(boolean value) {
        byteBuf.writeBoolean(value);
    }

    public void writeBooleanList(List<Boolean> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (boolean booleanValue : value) {
            writeBoolean(booleanValue);
        }
    }

    public void writeBooleanArray(boolean[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (boolean booleanValue : value) {
            writeBoolean(booleanValue);
        }
    }

    public void writeChar(char value) {
        byteBuf.writeChar(value);
    }

    public void writeCharList(List<Character> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (char charValue : value) {
            writeChar(charValue);
        }
    }

    public void writeCharArray(char[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (char charValue : value) {
            writeChar(charValue);
        }
    }

    public void writeString(String value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }else if (value.isEmpty()) {
            writeInteger(0);
            return;
        }
        byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);
        int length = strBytes.length;
        writeInteger(length);
        byteBuf.writeBytes(strBytes);
    }

    public void writeStringList(List<String> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (String stringValue : value) {
            writeString(stringValue);
        }
    }

    public void writeStringArray(String[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (String stringValue : value) {
            writeString(stringValue);
        }
    }

    public void writeByte(byte value) {
        byteBuf.writeByte(value);
    }

    public void writeByteList(List<Byte> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (byte byteValue : value) {
            writeByte(byteValue);
        }
    }

    public void writeByteArray(byte[] bytes) {
        if (null == bytes) {
            writeInteger(-1);
            return;
        }
        writeInteger(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public void writeShort(short value) {
        byteBuf.writeShort(value);
    }

    public void writeShortList(List<Short> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (short shortValue : value) {
            writeShort(shortValue);
        }
    }

    public void writeShortArray(short[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (short shortValue : value) {
            writeShort(shortValue);
        }
    }

    public void writeInteger(int value) {
        ReplicatedUtil.writeVarInt(byteBuf, value);
    }

    public void writeIntList(List<Integer> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (int intValue : value) {
            writeInteger(intValue);
        }
    }

    public void writeIntArray(int[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (int intValue : value) {
            writeInteger(intValue);
        }
    }

    public void writeLong(long value) {
        byteBuf.writeLong(value);
    }

    public void writeLongList(List<Long> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (long longValue : value) {
            writeLong(longValue);
        }
    }

    public void writeLongArray(long[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (long longValue : value) {
            writeLong(longValue);
        }
    }

    public void writeFloat(float value) {
        byteBuf.writeFloat(value);
    }

    public void writeFloatList(List<Float> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (float floatValue : value) {
            writeFloat(floatValue);
        }
    }

    public void writeFloatArray(float[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (float floatValue : value) {
            writeFloat(floatValue);
        }
    }

    public void writeDouble(double value) {
        byteBuf.writeDouble(value);
    }

    public void writeDoubleList(List<Double> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (double doubleValue : value) {
            writeDouble(doubleValue);
        }
    }

    public void writeDoubleArray(double[] value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        writeInteger(value.length);
        for (double doubleValue : value) {
            writeDouble(doubleValue);
        }
    }

    public void writeObject(SerializeComponent value) {
        if (null == value) {
            writeBoolean(true);
            return;
        }
        writeBoolean(false);
        value.serialize(this);
    }

    public void writeObjList(List<? extends SerializeComponent> value) {
        if (null == value) {
            writeInteger(-1);
            return;
        }
        if (value.isEmpty()) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (SerializeComponent replicate : value) {
            writeObject(replicate);
        }
    }

    public void reset() {
        byteBuf.clear();
        handle.recycle(this);
    }

    public byte[] toBytes() {
        return SnapshotTools.byteBufToByteArray(byteBuf);
    }
}
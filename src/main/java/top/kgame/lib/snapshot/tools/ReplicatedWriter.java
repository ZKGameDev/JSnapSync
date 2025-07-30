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

    public void writeByte(byte value) {
        byteBuf.writeByte(value);
    }

    public void writeInteger(int value) {
        ReplicatedUtil.writeVarInt(byteBuf, value);
    }


    public void writeLong(long value) {
        byteBuf.writeLong(value);
    }

    public void writeFloat(float value) {
        byteBuf.writeFloat(value);
    }
    public void writeDouble(double value) {
        byteBuf.writeDouble(value);
    }

    public void writeBytes(byte[] bytes) {
        byteBuf.writeBytes(bytes);
    }

    public void writeString(String value) {
        byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);
        int length = strBytes.length;
        writeInteger(length);
        byteBuf.writeBytes(strBytes);
    }

    public void writeListObj(List<? extends SerializeComponent> value) {
        if (null == value) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (SerializeComponent replicate : value) {
            replicate.serialize(this);
        }
    }

    public void writeListInt(List<Integer> value) {
        if (null == value) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (int intValue : value) {
            writeInteger(intValue);
        }
    }

    public void writeListLong(List<Long> value) {
        if (null == value) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (long longValue : value) {
            writeLong(longValue);
        }
    }

    public void writeListFloat(List<Float> value) {
        if (null == value) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (float floatValue : value) {
            writeFloat(floatValue);
        }
    }

    public void writeListDouble(List<Double> value) {
        if (null == value) {
            writeInteger(0);
            return;
        }
        int length = value.size();
        writeInteger(length);
        for (double doubleValue : value) {
            writeDouble(doubleValue);
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
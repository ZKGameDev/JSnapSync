package top.kgame.lib.snapshot.tools;

import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public byte readByte() {
        return byteBuf.readByte();
    }
    public char readChar() {
        return byteBuf.readChar();
    }
    public short readShort() {
        return byteBuf.readShort();
    }
    public int readInteger() {
        return ReplicatedUtil.readVarInt(byteBuf);
    }
    public long readLong() {
        return byteBuf.readLong();
    }
    public float readFloat() {
        return byteBuf.readFloat();
    }
    public double readDouble() {
        return byteBuf.readDouble();
    }
    public byte[] readByteArray() {
        int length = readInteger();
        if (length == 0) {
            return null;
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return bytes;
    }
    public String readString() {
        int length = readInteger();
        if (length == 0) {
            return null;
        }
        byte[] data = SnapshotTools.byteBufToByteArray(byteBuf.readBytes(length));
        return new String(data, StandardCharsets.UTF_8);
    }

    public List<Integer> readIntList() {
        int size = readInteger();
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readInteger());
        }
        return result;
    }

    private void readLayerLongList(List<? super List> list, int layer, ReplicatedReader reader) {
        if (layer < 2) {
            throw new IllegalArgumentException(String.format("layer must >= 2, current is %s!", layer));
        }
        if (layer == 2) {
            int size = reader.readInteger();
            for (int i = 0; i < size; i++) {
                list.add(reader.readLongList());
            }
            return;
        }
        int size = reader.readInteger();
        for (int i = 0; i < size; i++) {
            List<? super List> layerList = new ArrayList<>();
            list.add(layerList);
            readLayerLongList(layerList, layer - 1, reader);
        }
    }

    public List<Long> readLongList() {
        int size = readInteger();
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readLong());
        }
        return result;
    }

    public List<Float> readFloatList() {
        int size = readInteger();
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readFloat());
        }
        return result;
    }

    public List<Double> readDoubleList() {
        int size = readInteger();
        if (0 == size) {
            return Collections.emptyList();
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readDouble());
        }
        return result;
    }

    public List<String> readStringList() {
        int size = readInteger();
        if (0 == size) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(readString());
        }
        return result;
    }
}

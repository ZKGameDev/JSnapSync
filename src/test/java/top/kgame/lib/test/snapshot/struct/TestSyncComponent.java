package top.kgame.lib.test.snapshot.struct;

import top.kgame.lib.snapshot.DeserializeComponent;
import top.kgame.lib.snapshot.SerializeComponent;
import top.kgame.lib.snapshot.tools.ReplicatedReader;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;

import java.util.Objects;

public class TestSyncComponent implements SerializeComponent, DeserializeComponent {
    private byte b;
    private char c;
    private boolean bl;
    private short int16;
    private int int32;
    private long int64;
    private float f32;
    private double f64;
    private byte[] bs;
    private String ss;
    private TestSyncStruct struct;

    @Override
    public void serialize(ReplicatedWriter writer) {
        writer.writeByte(b);
        writer.writeChar(c);
        writer.writeBoolean(bl);
        writer.writeShort(int16);
        writer.writeInteger(int32);
        writer.writeLong(int64);
        writer.writeFloat(f32);
        writer.writeDouble(f64);
        writer.writeByteArray(bs);
        writer.writeString(ss);
        if (struct == null) {
            writer.writeInteger(-1);
        } else {
            writer.writeInteger(1);
            struct.serialize(writer);
        }

    }

    @Override
    public void deserialize(ReplicatedReader reader) {
        b = reader.readByte();
        c = reader.readChar();
        bl = reader.readBoolean();
        int16 = reader.readShort();
        int32 = reader.readInteger();
        int64 = reader.readLong();
        f32 = reader.readFloat();
        f64 = reader.readDouble();
        bs = reader.readByteArray();
        ss = reader.readString();
        int objNullType = reader.readInteger();
        if (objNullType < 0) {
            struct = null;
        } else {
            struct = new TestSyncStruct();
            struct.deserialize(reader);
        }
    }

    @Override
    public Integer getTypeId() {
        return 1;
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public boolean isBl() {
        return bl;
    }

    public void setBl(boolean bl) {
        this.bl = bl;
    }

    public short getInt16() {
        return int16;
    }

    public void setInt16(short int16) {
        this.int16 = int16;
    }

    public int getInt32() {
        return int32;
    }

    public void setInt32(int int32) {
        this.int32 = int32;
    }

    public long getInt64() {
        return int64;
    }

    public void setInt64(long int64) {
        this.int64 = int64;
    }

    public float getF32() {
        return f32;
    }

    public void setF32(float f32) {
        this.f32 = f32;
    }

    public double getF64() {
        return f64;
    }

    public void setF64(double f64) {
        this.f64 = f64;
    }

    public byte[] getBs() {
        return bs;
    }

    public void setBs(byte[] bs) {
        this.bs = bs;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public TestSyncStruct getStruct() {
        return struct;
    }

    public void setStruct(TestSyncStruct struct) {
        this.struct = struct;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestSyncComponent that = (TestSyncComponent) o;
        return b == that.b && c == that.c && bl == that.bl && int16 == that.int16 && int32 == that.int32 && int64 == that.int64 && Float.compare(f32, that.f32) == 0 && Double.compare(f64, that.f64) == 0 && Objects.deepEquals(bs, that.bs) && Objects.equals(ss, that.ss) && Objects.equals(struct, that.struct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(b, c, bl, int16, int32, int64);
    }
}
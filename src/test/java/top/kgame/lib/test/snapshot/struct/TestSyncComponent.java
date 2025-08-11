package top.kgame.lib.test.snapshot.struct;

import top.kgame.lib.snapshot.DeserializeComponent;
import top.kgame.lib.snapshot.SerializeComponent;
import top.kgame.lib.snapshot.tools.ReplicatedReader;
import top.kgame.lib.snapshot.tools.ReplicatedWriter;

import java.util.Arrays;
import java.util.List;
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
    // List 类型
    private List<TestSyncStruct> listObj;
    private List<Boolean> listBoolean;
    private List<Byte> listByte;
    private List<Character> listChar;
    private List<Short> listShort;
    private List<Integer> listInt;
    private List<Long> listLong;
    private List<Float> listFloat;
    private List<Double> listDouble;
    private List<String> listString;
    
    // 数组类型
    private boolean[] booleanArray;
    private char[] charArray;
    private short[] shortArray;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private String[] stringArray;

    @Override
    public void serialize(ReplicatedWriter writer) {
        // 基础类型
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
        writer.writeObject(struct);
        
        // List 类型
        writer.writeObjList(listObj);
        writer.writeBooleanList(listBoolean);
        writer.writeByteList(listByte);
        writer.writeCharList(listChar);
        writer.writeShortList(listShort);
        writer.writeIntList(listInt);
        writer.writeLongList(listLong);
        writer.writeFloatList(listFloat);
        writer.writeDoubleList(listDouble);
        writer.writeStringList(listString);
        
        // 数组类型
        writer.writeBooleanArray(booleanArray);
        writer.writeCharArray(charArray);
        writer.writeShortArray(shortArray);
        writer.writeIntArray(intArray);
        writer.writeLongArray(longArray);
        writer.writeFloatArray(floatArray);
        writer.writeDoubleArray(doubleArray);
        writer.writeStringArray(stringArray);
    }

    @Override
    public void deserialize(ReplicatedReader reader) {
        // 基础类型
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
        struct = reader.readObject(TestSyncStruct.class);
        
        // List 类型
        listObj = reader.readObjList(TestSyncStruct.class);
        listBoolean = reader.readBooleanList();
        listByte = reader.readByteList();
        listChar = reader.readCharList();
        listShort = reader.readShortList();
        listInt = reader.readIntList();
        listLong = reader.readLongList();
        listFloat = reader.readFloatList();
        listDouble = reader.readDoubleList();
        listString = reader.readStringList();
        
        // 数组类型
        booleanArray = reader.readBooleanArray();
        charArray = reader.readCharArray();
        shortArray = reader.readShortArray();
        intArray = reader.readIntArray();
        longArray = reader.readLongArray();
        floatArray = reader.readFloatArray();
        doubleArray = reader.readDoubleArray();
        stringArray = reader.readStringArray();
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

    // List 类型的 getters 和 setters
    public List<TestSyncStruct> getListObj() {
        return listObj;
    }

    public void setListObj(List<TestSyncStruct> listObj) {
        this.listObj = listObj;
    }

    public List<Boolean> getListBoolean() {
        return listBoolean;
    }

    public void setListBoolean(List<Boolean> listBoolean) {
        this.listBoolean = listBoolean;
    }

    public List<Byte> getListByte() {
        return listByte;
    }

    public void setListByte(List<Byte> listByte) {
        this.listByte = listByte;
    }

    public List<Character> getListChar() {
        return listChar;
    }

    public void setListChar(List<Character> listChar) {
        this.listChar = listChar;
    }

    public List<Short> getListShort() {
        return listShort;
    }

    public void setListShort(List<Short> listShort) {
        this.listShort = listShort;
    }

    public List<Integer> getListInt() {
        return listInt;
    }

    public void setListInt(List<Integer> listInt) {
        this.listInt = listInt;
    }

    public List<Long> getListLong() {
        return listLong;
    }

    public void setListLong(List<Long> listLong) {
        this.listLong = listLong;
    }

    public List<Float> getListFloat() {
        return listFloat;
    }

    public void setListFloat(List<Float> listFloat) {
        this.listFloat = listFloat;
    }

    public List<Double> getListDouble() {
        return listDouble;
    }

    public void setListDouble(List<Double> listDouble) {
        this.listDouble = listDouble;
    }

    public List<String> getListString() {
        return listString;
    }

    public void setListString(List<String> listString) {
        this.listString = listString;
    }

    // 数组类型的 getters 和 setters
    public boolean[] getBooleanArray() {
        return booleanArray;
    }

    public void setBooleanArray(boolean[] booleanArray) {
        this.booleanArray = booleanArray;
    }

    public char[] getCharArray() {
        return charArray;
    }

    public void setCharArray(char[] charArray) {
        this.charArray = charArray;
    }

    public short[] getShortArray() {
        return shortArray;
    }

    public void setShortArray(short[] shortArray) {
        this.shortArray = shortArray;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public void setIntArray(int[] intArray) {
        this.intArray = intArray;
    }

    public long[] getLongArray() {
        return longArray;
    }

    public void setLongArray(long[] longArray) {
        this.longArray = longArray;
    }

    public float[] getFloatArray() {
        return floatArray;
    }

    public void setFloatArray(float[] floatArray) {
        this.floatArray = floatArray;
    }

    public double[] getDoubleArray() {
        return doubleArray;
    }

    public void setDoubleArray(double[] doubleArray) {
        this.doubleArray = doubleArray;
    }

    public String[] getStringArray() {
        return stringArray;
    }

    public void setStringArray(String[] stringArray) {
        this.stringArray = stringArray;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestSyncComponent that = (TestSyncComponent) o;
        return b == that.b
                && c == that.c
                && bl == that.bl
                && int16 == that.int16
                && int32 == that.int32
                && int64 == that.int64
                && Float.compare(f32, that.f32) == 0
                && Double.compare(f64, that.f64) == 0
                && Objects.deepEquals(bs, that.bs)
                && Objects.equals(ss, that.ss)
                && Objects.equals(struct, that.struct)
                && Objects.equals(listObj, that.listObj)
                && Objects.equals(listBoolean, that.listBoolean)
                && Objects.equals(listByte, that.listByte)
                && Objects.equals(listChar, that.listChar)
                && Objects.equals(listShort, that.listShort)
                && Objects.equals(listInt, that.listInt)
                && Objects.equals(listLong, that.listLong)
                && Objects.equals(listFloat, that.listFloat)
                && Objects.equals(listDouble, that.listDouble)
                && Objects.equals(listString, that.listString)
                && Objects.deepEquals(booleanArray, that.booleanArray)
                && Objects.deepEquals(charArray, that.charArray)
                && Objects.deepEquals(shortArray, that.shortArray)
                && Objects.deepEquals(intArray, that.intArray)
                && Objects.deepEquals(longArray, that.longArray)
                && Objects.deepEquals(floatArray, that.floatArray)
                && Objects.deepEquals(doubleArray, that.doubleArray)
                && Objects.deepEquals(stringArray, that.stringArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(b, c, bl, int16, int32, int64, f32, f64, ss, struct,
                                  listObj, listBoolean, listByte, listChar, listShort, 
                                  listInt, listLong, listFloat, listDouble, listString);
        result = 31 * result + Arrays.hashCode(bs);
        result = 31 * result + Arrays.hashCode(booleanArray);
        result = 31 * result + Arrays.hashCode(charArray);
        result = 31 * result + Arrays.hashCode(shortArray);
        result = 31 * result + Arrays.hashCode(intArray);
        result = 31 * result + Arrays.hashCode(longArray);
        result = 31 * result + Arrays.hashCode(floatArray);
        result = 31 * result + Arrays.hashCode(doubleArray);
        result = 31 * result + Arrays.hashCode(stringArray);
        return result;
    }
}
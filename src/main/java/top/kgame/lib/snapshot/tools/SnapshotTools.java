package top.kgame.lib.snapshot.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import top.kgame.lib.snapshot.core.EntitySnapshotTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnapshotTools {
    public static final int BYTE_BUF_SIZE_LARGE = 10240;
    public static final int BYTE_BUF_SIZE_BIG = 1024;
    public static final int BYTE_BUF_SIZE_MIDDLE = 512;
    public static final int BYTE_BUF_SIZE_SMALL = 256;

    public static ByteBuf getByteBuf(int size) {
        return PooledByteBufAllocator.DEFAULT.heapBuffer(size);
    }

    public static byte[] byteBufToByteArray(ByteBuf byteBuf) {
        if (null == byteBuf) {
            return null;
        }
        byte[] result = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(result, 0, result.length);
        return result;
    }

    public static boolean compareByteSame(byte[] preData, byte[] newData) {
        if (preData.length != newData.length) {
            return false;
        }
        for (int i = 0; i< preData.length; i++) {
            if (preData[i] != newData[i]) {
                return false;
            }
        }
        return true;
    }

    public static void resetByteBuf(ByteBuf output) {
        output.resetReaderIndex();
        output.resetWriterIndex();
    }
}

package top.kgame.lib.snapshot.tools;

import io.netty.buffer.ByteBuf;
import top.kgame.lib.snapshot.core.EntitySnapshotTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnapshotTools {

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

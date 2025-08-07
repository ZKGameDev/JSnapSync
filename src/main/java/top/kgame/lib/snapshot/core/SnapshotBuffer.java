package top.kgame.lib.snapshot.core;

import io.netty.buffer.ByteBuf;
import top.kgame.lib.snapshot.SnapshotConfig;

import java.util.Arrays;

class SnapshotBuffer {
    
    private static final int INIT_VALUE = -1;
    public static SnapshotBuffer generate() {
        return new SnapshotBuffer();
    }

    private final int[] sequencesCache = new int[SnapshotConfig.SnapshotBufferSize];
    private final Snapshot[] cache = new Snapshot[SnapshotConfig.SnapshotBufferSize];
    private SnapshotBuffer() {
        Arrays.fill(sequencesCache, INIT_VALUE);
    }

    private int getIndex(int sequence) {
        return sequence % cache.length;
    }

    public boolean hasSnapshot(int sequence) {
        if (sequence < 0) {
            return false;
        }
        return sequencesCache[getIndex(sequence)] == sequence;
    }

    public byte[] getSnapshotByteData(int sequence) {
        Snapshot snapshot = getSnapshot(sequence);
        if (null != snapshot) {
            return generateByteArray(snapshot);
        }
        return null;
    }

    private byte[] generateByteArray(Snapshot snapshot) {
        return snapshot.getFullSnapshot();
    }

    public void remove(int sequence) {
        int index = getIndex(sequence);
        if (sequencesCache[index] == sequence) {
            sequencesCache[index] = INIT_VALUE;
            cache[index] = null;
        }
    }

    public void clear() {
        Arrays.fill(sequencesCache, INIT_VALUE);
    }

    public void insert(int sequence, Snapshot snapshot) {
        int index = getIndex(sequence);
        sequencesCache[index] = sequence;
        cache[index] = snapshot;
    }

    public Snapshot getSnapshot(int sequence) {
        int index = getIndex(sequence);
        if (sequencesCache[index] == sequence) {
            return cache[index];
        }
        return null;
    }
}

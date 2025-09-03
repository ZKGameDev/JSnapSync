package top.kgame.lib.test.snapshot.struct;

import top.kgame.lib.snapshot.DeserializeEntity;
import top.kgame.lib.snapshot.DeserializeFactory;
import top.kgame.lib.snapshot.SnapshotConnection;

import java.util.Collection;

public class TestSnapshotClient extends SnapshotConnection {
    public TestSnapshotClient(long uid, TestSnapshotServer server) {
        super(uid, server);
    }

    @Override
    protected void sendAdditionSnapshot(int inSequence, int outSequence, byte[] updateBytes, Collection<Integer> createIds, Collection<Integer> destroyIds) {

    }

    @Override
    protected void sendFullSnapshot(int inSequence, int outSequence, byte[] updateBytes, Collection<Integer> createIds) {

    }

    @Override
    protected void receive(int inSequence, DeserializeEntity deserializeEntity) {

    }
}
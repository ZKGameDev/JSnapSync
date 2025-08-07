package top.kgame.lib.test.snapshot.struct;

import top.kgame.lib.snapshot.DeserializeEntity;
import top.kgame.lib.snapshot.SerializeComponent;
import top.kgame.lib.snapshot.SerializeEntity;
import top.kgame.lib.snapshot.tools.ReplicatedReader;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestSyncEntity implements SerializeEntity, DeserializeEntity {
    private final List<SerializeComponent> components = new ArrayList<>();
    private final int guid;
    private final int type;
    public TestSyncEntity(int guid, int type) {
        this.guid = guid;
        this.type = type;
    }
    @Override
    public void deserialize(ReplicatedReader reader) {

    }

    @Override
    public Collection<SerializeComponent> getComponents() {
        return components;
    }

    @Override
    public int getGuid() {
        return guid;
    }

    @Override
    public int getTypeId() {
        return type;
    }

    public void addComponent(TestSyncComponent component) {
        components.add(component);
    }
}
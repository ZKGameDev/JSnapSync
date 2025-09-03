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
    private int guid;
    private int type;
    public TestSyncEntity(int guid, int type) {
        this.guid = guid;
        this.type = type;
    }
    @Override
    public void deserialize(ReplicatedReader reader) {

    }

    @Override
    public void setGuid(int id) {
        this.guid = id;
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
    public void setTypeId(int type) {
        this.type = type;
    }

    @Override
    public int getTypeId() {
        return type;
    }

    public void addComponent(TestSyncComponent component) {
        components.add(component);
    }
}
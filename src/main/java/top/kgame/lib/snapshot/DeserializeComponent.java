package top.kgame.lib.snapshot;

import top.kgame.lib.snapshot.tools.ReplicatedReader;

public interface DeserializeComponent {
    void deserialize(ReplicatedReader reader);
}

package de.groodian.hyperiorcore.spawnable;

import java.util.ArrayList;
import java.util.List;

public class SpawnAbleManager {

    private final List<SpawnAble> spawnAbleList;

    public SpawnAbleManager() {
        spawnAbleList = new ArrayList<>();
    }

    public void registerSpawnAble(SpawnAble spawnAble) {
        spawnAbleList.add(spawnAble);
    }

    public List<SpawnAble> getSpawnAbleList() {
        return spawnAbleList;
    }

}

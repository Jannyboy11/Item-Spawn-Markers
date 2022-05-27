package com.janboerman.itemspawnmarkers;

import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class ItemSpawnList implements Iterable<ItemSpawn> {

    private final Map<SpawnKey, Integer> maxCooldowns = new HashMap<>();

    public boolean contains(SpawnKey spawnKey) {
        return maxCooldowns.containsKey(spawnKey);
    }

    public Integer getRespawnTime(SpawnKey spawnKey) {
        return maxCooldowns.get(spawnKey);
    }

    public ItemSpawn getItemSpawn(SpawnKey spawnKey) {
        Integer respawnTime = getRespawnTime(spawnKey);
        if (respawnTime == null) return null;
        return new ItemSpawn(spawnKey.getLocation(), spawnKey.getItemId(), respawnTime);
    }

    public boolean put(ItemSpawn itemSpawn) {
        SpawnKey spawnKey = new SpawnKey(itemSpawn.getLocation(), itemSpawn.getItemId());
        int ticks = itemSpawn.getRespawnTime();
        return maxCooldowns.put(spawnKey, ticks) == null;
    }

    public boolean remove(ItemSpawn itemSpawn) {
        SpawnKey spawnKey = new SpawnKey(itemSpawn.getLocation(), itemSpawn.getItemId());
        int ticks = itemSpawn.getRespawnTime();
        return maxCooldowns.remove(spawnKey, ticks);
    }

    @Override
    public java.util.Iterator<ItemSpawn> iterator() {
        return new Iterator(maxCooldowns.entrySet().iterator());
    }

    private static class Iterator implements java.util.Iterator<ItemSpawn> {

        private final java.util.Iterator<Entry<SpawnKey, Integer>> entryIterator;

        private Iterator(java.util.Iterator<Entry<SpawnKey, Integer>> entryIterator) {
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @Override
        public ItemSpawn next() {
            Entry<SpawnKey, Integer> entry = entryIterator.next();
            SpawnKey spawnKey = entry.getKey();
            WorldPoint location = spawnKey.getLocation();
            int itemId = spawnKey.getItemId();
            int coolDown = entry.getValue();
            return new ItemSpawn(location, itemId, coolDown);
        }

        @Override
        public void remove() {
            entryIterator.remove();
        }
    }
}


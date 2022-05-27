package com.janboerman.itemspawnmarkers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class ItemSpawnCache {

    private final Map<ActiveSpawn, Integer> countdowns = new HashMap<>();

    ItemSpawnCache() {
    }

    void itemPickedUp(final ItemSpawn itemSpawn, final int world) {
        final ActiveSpawn activeSpawn = new ActiveSpawn(itemSpawn.getLocation(), world, itemSpawn.getItemId());
        countdowns.put(activeSpawn, itemSpawn.getRespawnTime());
    }

    void itemAppeared(final ItemSpawn itemSpawn, final int world) {
        final ActiveSpawn activeSpawn = new ActiveSpawn(itemSpawn.getLocation(), world, itemSpawn.getItemId());
        countdowns.remove(activeSpawn);
    }

    void tick() {
        Iterator<Entry<ActiveSpawn, Integer>> iterator = countdowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<ActiveSpawn, Integer> entry = iterator.next();
            int newValue = entry.getValue().intValue() - 1;
            if (newValue == 0) {
                iterator.remove();
            } else {
                entry.setValue(Integer.valueOf(newValue));
            }
        }
    }

    ItemSpawnCooldown getCooldown(ActiveSpawn spawn, boolean itemPresent) {
        if (itemPresent) {
            return Cooldown.available();
        } else {
            Integer cooldown = countdowns.get(spawn);
            if (cooldown != null) {
                return ItemSpawnCooldown.ofTicks(cooldown.intValue());
            } else {
                return ItemSpawnCooldown.unknown();
            }
        }
    }

    void clear() {
        countdowns.clear();
    }

}


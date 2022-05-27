package com.janboerman.itemspawnmarkers;

import lombok.Value;

class ItemSpawnCooldown {

    ItemSpawnCooldown() {
    }

    static ItemSpawnCooldown ofTicks(int ticks) {
        return new Cooldown(ticks);
    }

    static ItemSpawnCooldown unknown() {
        return UnknownCooldown.INSTANCE;
    }

    static ItemSpawnCooldown available() {
        return CooledDown.INSTANCE;
    }

}

@Value
class Cooldown extends ItemSpawnCooldown {
    private final int ticks;
}

class UnknownCooldown extends ItemSpawnCooldown {

    static final UnknownCooldown INSTANCE = new UnknownCooldown();

    private UnknownCooldown() {}
}

class CooledDown extends ItemSpawnCooldown {

    static final CooledDown INSTANCE = new CooledDown();

    private CooledDown() {}
}
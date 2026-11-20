package com.zver.adaptivemobs.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

/** World-persistent evolution clock. One stage lasts ten minutes of game time. */
public class EvolutionData extends SavedData {
    public static final int MAX_STAGE = 10;
    private static final int TICKS_PER_STAGE = 12_000;
    private static final String ELAPSED_TICKS = "elapsed_ticks";
    private long elapsedTicks;

    public static EvolutionData load(CompoundTag tag) {
        EvolutionData data = new EvolutionData();
        data.elapsedTicks = tag.getLong(ELAPSED_TICKS);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong(ELAPSED_TICKS, elapsedTicks);
        return tag;
    }

    public int stage() {
        return Math.min(MAX_STAGE, (int) (elapsedTicks / TICKS_PER_STAGE) + 1);
    }

    public long elapsedTicks() {
        return elapsedTicks;
    }

    public void advanceOneSecond() {
        if (stage() < MAX_STAGE) {
            elapsedTicks += 20;
            setDirty();
        }
    }
}

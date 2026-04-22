package eu.cizmetari.bgm.additions;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.function.Supplier;

public class LoadPersistentObject<T> {
    private T value;
    private final Supplier<T> factory;

    public LoadPersistentObject(Supplier<T> factory) {
        this.factory = factory;
        this.value = factory.get();
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @SubscribeEvent
    public void clear(LevelEvent.Unload ctx) {
        this.value = factory.get();
    }
}

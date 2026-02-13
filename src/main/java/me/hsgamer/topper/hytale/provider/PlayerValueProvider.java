package me.hsgamer.topper.hytale.provider;

import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.topper.value.core.ValueWrapper;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PlayerValueProvider implements PlayerWorldValueProvider<Double> {
    private final Type valueType;

    public PlayerValueProvider(Map<String, Object> settings) {
        this.valueType = Optional.ofNullable(settings.get("value"))
                .map(Objects::toString)
                .map(s -> {
                    try {
                        return Type.valueOf(s.toUpperCase());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    @Override
    public void accept(PlayerRef playerRef, Ref<EntityStore> entityRef, Store<EntityStore> entityStore, Consumer<ValueWrapper<Double>> callback) {
        switch (valueType) {
            case MEMORY -> {
                PlayerMemories playerMemories = entityStore.getComponent(entityRef, PlayerMemories.getComponentType());
                if (playerMemories == null) {
                    callback.accept(ValueWrapper.notHandled());
                    return;
                }
                double memoryCount = playerMemories.getRecordedMemories().size();
                callback.accept(ValueWrapper.handled(memoryCount));
            }
            default -> callback.accept(ValueWrapper.notHandled());
        }
    }

    @Override
    public boolean isValid() {
        return valueType != null;
    }

    public enum Type {
        MEMORY
    }
}

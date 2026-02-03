package me.hsgamer.topperstale.provider;

import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PlayerValueProvider implements ValueProvider<UUID, Double> {
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
    public @Nonnull ValueWrapper<Double> apply(@Nonnull UUID uuid) {
        if (valueType == null) return ValueWrapper.notHandled();
        PlayerRef player = Universe.get().getPlayer(uuid);
        if (player == null) return ValueWrapper.notHandled();
        Holder<EntityStore> holder = player.getHolder();
        if (holder == null) return ValueWrapper.notHandled();
        return switch (valueType) {
            case MEMORY -> {
                PlayerMemories playerMemories = holder.getComponent(PlayerMemories.getComponentType());
                if (playerMemories == null) yield ValueWrapper.notHandled();
                double memoryCount = playerMemories.getRecordedMemories().size();
                yield ValueWrapper.handled(memoryCount);
            }
            default -> ValueWrapper.notHandled();
        };
    }

    public enum Type {
        MEMORY
    }
}

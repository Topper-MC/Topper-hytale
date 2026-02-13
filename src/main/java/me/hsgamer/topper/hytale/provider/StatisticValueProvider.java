package me.hsgamer.topper.hytale.provider;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.topper.value.core.ValueWrapper;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class StatisticValueProvider implements PlayerWorldValueProvider<Double> {
    private final String statistic;
    private final StatisticMode mode;

    public StatisticValueProvider(Map<String, Object> map) {
        this.statistic = Optional.ofNullable(map.get("statistic"))
                .map(Objects::toString)
                .orElse(null);
        this.mode = Optional.ofNullable(map.get("mode"))
                .map(Objects::toString)
                .map(s -> {
                    try {
                        return StatisticMode.valueOf(s.toUpperCase(Locale.ROOT));
                    } catch (Exception e) {
                        return null;
                    }
                })
                .orElse(StatisticMode.VALUE);
    }

    @Override
    public void accept(PlayerRef playerRef, Ref<EntityStore> entityRef, Store<EntityStore> entityStore, Consumer<ValueWrapper<Double>> callback) {
        EntityStatMap entityStatMap = entityStore.getComponent(entityRef, EntityStatMap.getComponentType());
        if (entityStatMap == null) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        int statIndex = EntityStatType.getAssetMap().getIndex(statistic);
        EntityStatValue statValue = entityStatMap.get(statIndex);
        if (statValue == null) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        double value = switch (mode) {
            case MAX -> statValue.getMax();
            case MIN -> statValue.getMin();
            default -> statValue.get();
        };
        callback.accept(ValueWrapper.handled(value));
    }

    @Override
    public boolean isValid() {
        return statistic != null;
    }

    public enum StatisticMode {
        VALUE,
        MIN,
        MAX
    }
}

package me.hsgamer.topper.hytale.manager;

import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.topper.hytale.TopperPlugin;
import me.hsgamer.topper.hytale.provider.PlayerValueProvider;
import me.hsgamer.topper.hytale.provider.StatisticValueProvider;
import me.hsgamer.topper.value.core.ValueProvider;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ValueProviderManager extends FunctionalMassBuilder<Map<String, Object>, ValueProvider<UUID, Double>> {
    public ValueProviderManager(TopperPlugin plugin) {
        register(PlayerValueProvider::new, "player");
        register(StatisticValueProvider::new, "statistic");
    }

    @Override
    protected String getType(Map<String, Object> map) {
        return Objects.toString(map.get("type"), "");
    }
}

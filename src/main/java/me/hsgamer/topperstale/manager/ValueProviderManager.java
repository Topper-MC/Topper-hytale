package me.hsgamer.topperstale.manager;

import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topperstale.ToppersTale;
import me.hsgamer.topperstale.provider.PlayerValueProvider;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ValueProviderManager extends FunctionalMassBuilder<Map<String, Object>, ValueProvider<UUID, Double>> {
    public ValueProviderManager(ToppersTale plugin) {
        register(PlayerValueProvider::new, "player");
    }

    @Override
    protected String getType(Map<String, Object> map) {
        return Objects.toString(map.get("type"), "");
    }
}

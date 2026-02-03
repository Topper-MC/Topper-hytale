package me.hsgamer.topperstale.template;

import me.hsgamer.topper.template.topplayernumber.holder.display.ValueDisplay;

import java.util.Map;
import java.util.Optional;

public record HyValueDisplaySettings(Map<String, Object> map) implements ValueDisplay.Settings {
    @Override
    public String defaultLine() {
        return Optional.ofNullable(map.get("line"))
                .map(Object::toString)
                .orElse("[{index}] {name} - {value}");
    }

    @Override
    public String displayNullName() {
        return Optional.ofNullable(map.get("null-name"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullUuid() {
        return Optional.ofNullable(map.get("null-uuid"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullValue() {
        return Optional.ofNullable(map.get("null-value"))
                .map(Object::toString)
                .orElse("---");
    }
}

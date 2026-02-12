package me.hsgamer.topper.hytale.config.converter;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.topper.hytale.template.HyTopHolderSettings;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;

public class HolderMapConverter extends StringMapConverter<NumberTopHolder.Settings> {
    @Override
    protected NumberTopHolder.Settings toValue(Object value) {
        return MapUtils.castOptionalStringObjectMap(value).map(HyTopHolderSettings::new).orElse(null);
    }

    @Override
    protected Object toRawValue(Object value) {
        if (value instanceof HyTopHolderSettings hyTopHolderSettings) {
            return hyTopHolderSettings.map();
        }
        return null;
    }
}

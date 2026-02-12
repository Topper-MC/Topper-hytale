package me.hsgamer.topper.hytale.template;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.hytale.TopperPlugin;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.converter.NumberFlatValueConverter;
import me.hsgamer.topper.storage.flat.converter.UUIDFlatValueConverter;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.template.topplayernumber.TopPlayerNumberTemplate;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import me.hsgamer.topper.template.topplayernumber.manager.ReloadManager;
import me.hsgamer.topper.value.core.ValueProvider;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class HyTopTemplate extends TopPlayerNumberTemplate {
    private final TopperPlugin plugin;

    public HyTopTemplate(TopperPlugin plugin) {
        super(new Settings() {
            @Override
            public Map<String, NumberTopHolder.Settings> holders() {
                return plugin.getMainConfig().getHolders();
            }

            @Override
            public int taskSaveEntryPerTick() {
                return plugin.getMainConfig().getTaskSaveEntryPerTick();
            }

            @Override
            public int taskUpdateEntryPerTick() {
                return plugin.getMainConfig().getTaskUpdateEntryPerTick();
            }

            @Override
            public int taskUpdateMaxSkips() {
                return plugin.getMainConfig().getTaskUpdateMaxSkips();
            }
        });
        this.plugin = plugin;
        getReloadManager().add(new ReloadManager.ReloadEntry() {
            @Override
            public void reload() {
                plugin.getMainConfig().reloadConfig();
            }
        });
    }

    @Override
    public Function<String, DataStorage<UUID, Double>> getStorageSupplier() {
        var keyConverter = new UUIDFlatValueConverter();
        var valueConverter = new NumberFlatValueConverter<>(number -> number != null ? number.doubleValue() : 0);
        return switch (plugin.getMainConfig().getStorageType().toLowerCase(Locale.ROOT)) {
            case "json" ->
                    name -> new ConfigFileDataStorage<>(plugin.getDataDirectory().toFile(), name, keyConverter, valueConverter) {
                        @Override
                        protected Config getConfig(File file) {
                            return new GsonConfig(file);
                        }

                        @Override
                        protected String getConfigName(String s) {
                            return name + ".json";
                        }
                    };
            default ->
                    name -> new PropertiesDataStorage<>(plugin.getDataDirectory().toFile(), name, keyConverter, valueConverter);
        };
    }

    @Override
    public Optional<ValueProvider<UUID, Double>> createValueProvider(Map<String, Object> settings) {
        return plugin.getValueProviderManager().build(settings);
    }

    @Override
    public Agent createTask(Runnable runnable, NumberTopHolder.TaskType taskType, Map<String, Object> settings) {
        switch (taskType) {
            case STORAGE -> {
                return plugin.getTaskManager().createTaskAgent(runnable, plugin.getMainConfig().getTaskSaveDelay(), true);
            }
            case SET -> {
                return plugin.getTaskManager().createTaskAgent(runnable, plugin.getMainConfig().getTaskUpdateSetDelay(), true);
            }
            case UPDATE -> {
                boolean async = Optional.ofNullable(settings.get("async"))
                        .map(Objects::toString)
                        .map(Boolean::parseBoolean)
                        .orElse(false);
                return plugin.getTaskManager().createTaskAgent(runnable, plugin.getMainConfig().getTaskUpdateDelay(), async);
            }
            default -> {
                return plugin.getTaskManager().createTaskAgent(runnable, 1000, true);
            }
        }
    }

    @Override
    public void logWarning(String message, @Nullable Throwable throwable) {
        plugin.getLogger().at(Level.WARNING).withCause(throwable).log(message);
    }
}

package me.hsgamer.topperstale.manager;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.server.core.HytaleServer;
import me.hsgamer.topperstale.ToppersTale;
import me.hsgamer.topperstale.hook.PlaceholderAPIHook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HookManager {
    private final Map<PluginIdentifier, Supplier<Hook>> hookMap = new HashMap<>();
    private final List<Hook> hooks = new ArrayList<>();

    public HookManager(ToppersTale plugin) {
        hookMap.put(new PluginIdentifier("HelpChat", "PlaceholderAPI"), () -> new PlaceholderAPIHook(plugin));
    }

    public void init() {
        for (Map.Entry<PluginIdentifier, Supplier<Hook>> entry : hookMap.entrySet()) {
            PluginIdentifier pluginIdentifier = entry.getKey();
            Supplier<Hook> supplier = entry.getValue();
            if (HytaleServer.get().getPluginManager().hasPlugin(pluginIdentifier, SemverRange.WILDCARD)) {
                hooks.add(supplier.get());
            }
        }
    }

    public void call(Consumer<Hook> consumer, boolean reverse) {
        if (reverse) {
            for (int i = hooks.size() - 1; i >= 0; i--) {
                consumer.accept(hooks.get(i));
            }
        } else {
            for (Hook hook : hooks) {
                consumer.accept(hook);
            }
        }
    }

    public void call(Consumer<Hook> consumer) {
        call(consumer, false);
    }

    public interface Hook {
        default void setup() {
            // EMPTY
        }

        default void start() {
            // EMPTY
        }

        default void shutdown() {
            // EMPTY
        }
    }
}

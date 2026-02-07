package me.hsgamer.topperstale.hook;

import at.helpch.placeholderapi.PlaceholderAPI;
import at.helpch.placeholderapi.expansion.PlaceholderExpansion;
import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.topper.query.forward.QueryForwardContext;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;
import me.hsgamer.topper.value.string.StringDeformatters;
import me.hsgamer.topperstale.ToppersTale;
import me.hsgamer.topperstale.manager.HookManager.Hook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaceholderAPIHook implements Hook {
    private final ToppersTale plugin;
    private final PlaceholderQueryForwarder queryForwarder;

    public PlaceholderAPIHook(ToppersTale plugin) {
        this.plugin = plugin;
        this.queryForwarder = new PlaceholderQueryForwarder(plugin);
    }

    @Override
    public void setup() {
        plugin.getValueProviderManager().register(map -> {
            String placeholder = Optional.ofNullable(map.get("placeholder")).map(Object::toString).orElse(null);
            if (placeholder == null) {
                return ValueProvider.error("'placeholder' field is required");
            }
            return new PlaceholderValueProvider(placeholder)
                    .thenApply(StringDeformatters.deformatterOrIdentity(map))
                    .thenApply(Double::parseDouble);
        }, "placeholder", "placeholderapi");
        plugin.getTopTemplate().getQueryForwardManager().addForwarder(queryForwarder);
    }

    @Override
    public void shutdown() {
        queryForwarder.unregister();
    }

    private record PlaceholderValueProvider(String placeholder) implements ValueProvider<UUID, String> {
        @Override
        public void accept(UUID uuid, Consumer<ValueWrapper<String>> callback) {
            if (placeholder == null) {
                callback.accept(ValueWrapper.notHandled());
                return;
            }
            PlayerRef player = Universe.get().getPlayer(uuid);
            if (player == null) {
                callback.accept(ValueWrapper.notHandled());
                return;
            }
            String parsed;
            try {
                parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
            } catch (Throwable throwable) {
                callback.accept(ValueWrapper.error(throwable));
                return;
            }
            callback.accept(ValueWrapper.handled(parsed.equals(placeholder) ? null : parsed));
        }
    }

    private static final class PlaceholderQueryForwarder implements Consumer<QueryForwardContext<UUID>> {
        private final List<PlaceholderExpansion> expansions = new ArrayList<>();
        private final PluginBase defaultPlugin;

        private PlaceholderQueryForwarder(PluginBase defaultPlugin) {
            this.defaultPlugin = defaultPlugin;
        }

        @Override
        public void accept(QueryForwardContext<UUID> context) {
            PluginBase plugin = defaultPlugin;
            PlaceholderExpansion expansion = new PlaceholderExpansion() {
                @Override
                public @Nullable String onPlaceholderRequest(PlayerRef playerRef, @NotNull String params) {
                    UUID uuid = null;
                    if (playerRef != null && playerRef.isValid()) {
                        Holder<EntityStore> holder = playerRef.getHolder();
                        if (holder != null) {
                            uuid = holder.ensureAndGetComponent(UUIDComponent.getComponentType()).getUuid();
                        }
                    }
                    return context.getQuery().apply(uuid, params).result;
                }

                @Override
                public @NotNull String getIdentifier() {
                    return context.getName();
                }

                @Override
                public @NotNull String getAuthor() {
                    return plugin.getManifest().getAuthors().stream().map(AuthorInfo::getName).collect(Collectors.joining(", "));
                }

                @Override
                public @NotNull String getVersion() {
                    return plugin.getManifest().getVersion().toString();
                }

                @Override
                public boolean persist() {
                    return true;
                }
            };
            expansion.register();
            expansions.add(expansion);
        }

        public void unregister() {
            expansions.forEach(PlaceholderExpansion::unregister);
            expansions.clear();
        }
    }
}

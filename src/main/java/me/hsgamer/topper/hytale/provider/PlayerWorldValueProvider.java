package me.hsgamer.topper.hytale.provider;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.topper.value.core.ValueProvider;
import me.hsgamer.topper.value.core.ValueWrapper;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerWorldValueProvider<V> extends ValueProvider<UUID, V> {
    void accept(PlayerRef playerRef, Ref<EntityStore> entityRef, Store<EntityStore> entityStore, Consumer<ValueWrapper<V>> callback);

    default boolean isValid() {
        return true;
    }

    @Override
    default void accept(UUID uuid, Consumer<ValueWrapper<V>> callback) {
        if (!isValid()) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        PlayerRef player = Universe.get().getPlayer(uuid);
        if (player == null) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        Ref<EntityStore> reference = player.getReference();
        if (reference == null || !reference.isValid()) {
            callback.accept(ValueWrapper.notHandled());
            return;
        }

        Store<EntityStore> store = reference.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> accept(player, reference, store, callback));
    }
}

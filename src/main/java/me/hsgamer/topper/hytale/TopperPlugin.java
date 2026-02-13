package me.hsgamer.topper.hytale;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.hytale.commands.ExampleCommand;
import me.hsgamer.topper.hytale.config.MainConfig;
import me.hsgamer.topper.hytale.manager.HookManager;
import me.hsgamer.topper.hytale.manager.TaskManager;
import me.hsgamer.topper.hytale.manager.ValueProviderManager;
import me.hsgamer.topper.hytale.template.HyTopTemplate;

import javax.annotation.Nonnull;

public class TopperPlugin extends JavaPlugin {
    private final MainConfig mainConfig;
    private final HyTopTemplate topTemplate;
    private final TaskManager taskManager;
    private final ValueProviderManager valueProviderManager;
    private final HookManager hookManager;

    public TopperPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        this.mainConfig = ConfigGenerator.newInstance(MainConfig.class, new GsonConfig(getDataDirectory().resolve("config.json").toFile(), new GsonBuilder().setPrettyPrinting().create()));
        this.taskManager = new TaskManager(this);
        this.valueProviderManager = new ValueProviderManager(this);
        this.topTemplate = new HyTopTemplate(this);
        this.hookManager = new HookManager(this);
    }

    @Override
    protected void setup() {
        hookManager.init();
        hookManager.call(HookManager.Hook::setup);
        topTemplate.enable();
        hookManager.call(HookManager.Hook::start);
        getCommandRegistry().registerCommand(new ExampleCommand("message", "Message Command"));
//        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
//            Ref<EntityStore> playerRef = event.getPlayerRef();
//            topTemplate.getTopManager().create(playerRef.getStore().ensureAndGetComponent(playerRef, UUIDComponent.getComponentType()).getUuid());
//        });
    }

    @Override
    protected void shutdown() {
        topTemplate.disable();
        taskManager.shutdown();
        hookManager.call(HookManager.Hook::shutdown);
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public HyTopTemplate getTopTemplate() {
        return topTemplate;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public ValueProviderManager getValueProviderManager() {
        return valueProviderManager;
    }
}
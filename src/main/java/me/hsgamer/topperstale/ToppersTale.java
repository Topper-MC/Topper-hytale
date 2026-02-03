package me.hsgamer.topperstale;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topperstale.commands.ExampleCommand;
import me.hsgamer.topperstale.config.MainConfig;
import me.hsgamer.topperstale.manager.TaskManager;
import me.hsgamer.topperstale.template.HyTopTemplate;

import javax.annotation.Nonnull;

public class ToppersTale extends JavaPlugin {
    private final MainConfig mainConfig;
    private final HyTopTemplate topTemplate;
    private final TaskManager taskManager;

    public ToppersTale(@Nonnull JavaPluginInit init) {
        super(init);
        this.mainConfig = ConfigGenerator.newInstance(MainConfig.class, new GsonConfig(getDataDirectory().resolve("config.json").toFile(), new GsonBuilder().setPrettyPrinting().create()));
        this.taskManager = new TaskManager(this);
        this.topTemplate = new HyTopTemplate(this);
    }

    @Override
    protected void setup() {
        topTemplate.enable();
        getCommandRegistry().registerCommand(new ExampleCommand("message", "Message Command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Ref<EntityStore> playerRef = event.getPlayerRef();
            topTemplate.getTopManager().create(playerRef.getStore().ensureAndGetComponent(playerRef, UUIDComponent.getComponentType()).getUuid());
        });
    }

    @Override
    protected void shutdown() {
        topTemplate.disable();
        taskManager.shutdown();
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
}
package me.hsgamer.topperstale;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topperstale.commands.ExampleCommand;
import me.hsgamer.topperstale.config.MainConfig;
import me.hsgamer.topperstale.events.ExampleEvent;
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
        this.topTemplate = new HyTopTemplate(this);
        this.taskManager = new TaskManager();
    }

    @Override
    protected void setup() {
        topTemplate.enable();
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);
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
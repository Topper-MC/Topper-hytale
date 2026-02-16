package me.hsgamer.topper.hytale.manager;

import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.hytale.TopperPlugin;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerNameManager {
    private final TopperPlugin plugin;
    private final Map<UUID, AtomicReference<String>> uuidToStringMap = new ConcurrentHashMap<>();
    private final Queue<UUID> pendingQueue = new ConcurrentLinkedQueue<>();
    private Agent fetchAgent;

    public PlayerNameManager(TopperPlugin plugin) {
        this.plugin = plugin;
    }

    private void runFetch() {
        UUID uuid = pendingQueue.poll();
        if (uuid == null) {
            return;
        }

        AtomicReference<String> nameRef = uuidToStringMap.get(uuid);
        if (nameRef == null || nameRef.get() != null) {
            return;
        }

        ServerAuthManager authManager = ServerAuthManager.getInstance();
        String sessionToken = authManager.getSessionToken();
        if (sessionToken == null) {
            return;
        }

        ProfileServiceClient profileClient = authManager.getProfileServiceClient();
        ProfileServiceClient.PublicGameProfile profile = profileClient.getProfileByUuid(uuid, sessionToken);
        if (profile == null) {
            return;
        }

        nameRef.set(profile.getUsername());
    }

    public void setup() {
        fetchAgent = plugin.getTaskManager().createTaskAgent(this::runFetch, 1000, true);
        fetchAgent.start();
    }

    public void shutdown() {
        if (fetchAgent != null) {
            fetchAgent.stop();
        }
    }

    public String getName(UUID uuid) {
        AtomicReference<String> nameRef = uuidToStringMap.get(uuid);
        boolean refNotExist = nameRef == null;
        if (refNotExist) {
            nameRef = new AtomicReference<>(null);
            uuidToStringMap.put(uuid, nameRef);
        }

        String name = nameRef.get();
        if (name != null) {
            return name;
        }

        PlayerRef player = Universe.get().getPlayer(uuid);
        if (player != null && player.isValid()) {
            String username = player.getUsername();
            nameRef.set(username);
            return username;
        }

        if (refNotExist) {
            pendingQueue.add(uuid);
        }

        return null;
    }
}

package me.hsgamer.topper.hytale.template;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HyTopHolderSettings extends NumberTopHolder.MapSettings {
    private final List<String> ignorePermissions;
    private final List<String> resetPermissions;

    public HyTopHolderSettings(Map<String, Object> map) {
        super(map);
        ignorePermissions = CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
        resetPermissions = CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
    }

    @Override
    public UpdateAgent.FilterResult filter(UUID uuid) {
        if (ignorePermissions.isEmpty() && resetPermissions.isEmpty()) {
            return UpdateAgent.FilterResult.CONTINUE;
        }
        PermissionsModule module = PermissionsModule.get();
        if (!resetPermissions.isEmpty() && resetPermissions.stream().anyMatch(permission -> module.hasPermission(uuid, permission))) {
            return UpdateAgent.FilterResult.RESET;
        }
        if (!ignorePermissions.isEmpty() && ignorePermissions.stream().anyMatch(permission -> module.hasPermission(uuid, permission))) {
            return UpdateAgent.FilterResult.SKIP;
        }
        return UpdateAgent.FilterResult.CONTINUE;
    }
}

package me.hsgamer.topperstale.template;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.topper.agent.update.UpdateAgent;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class HyTopHolderSettings implements NumberTopHolder.Settings {
    private final Map<String, Object> map;
    private final List<String> ignorePermissions;
    private final List<String> resetPermissions;

    public HyTopHolderSettings(Map<String, Object> map) {
        this.map = map;
        ignorePermissions = CollectionUtils.createStringListFromObject(map.get("ignore-permission"), true);
        resetPermissions = CollectionUtils.createStringListFromObject(map.get("reset-permission"), true);
    }

    @Override
    public Double defaultValue() {
        return Optional.ofNullable(map.get("default-value"))
                .map(Object::toString)
                .map(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .orElse(null);
    }

    @Override
    public String displayNullName() {
        return Optional.ofNullable(map.get("null-name"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullUuid() {
        return Optional.ofNullable(map.get("null-uuid"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public String displayNullValue() {
        return Optional.ofNullable(map.get("null-value"))
                .map(Object::toString)
                .orElse("---");
    }

    @Override
    public boolean showErrors() {
        return Optional.ofNullable(map.get("show-errors"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    @Override
    public boolean resetOnError() {
        return Optional.ofNullable(map.get("reset-on-error"))
                .map(Object::toString)
                .map(String::toLowerCase)
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    @Override
    public boolean reverse() {
        return Optional.ofNullable(map.get("reverse"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
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

    @Override
    public Map<String, Object> valueProvider() {
        return map;
    }

    public Map<String, Object> map() {
        return map;
    }
}

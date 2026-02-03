package me.hsgamer.topperstale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ExampleCommand extends AbstractCommand {
    private final RequiredArg<String> messageArgument;

    public ExampleCommand(String name, String description) {
        super(name, description);
        this.messageArgument = withRequiredArg("message", "Message", ArgTypes.STRING);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String message = context.get(messageArgument);
        context.sendMessage(Message.raw(message));
        return CompletableFuture.completedFuture(null);
    }

}

package me.hsgamer.topper.hytale.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ExampleCommand extends AbstractCommand {
    private final RequiredArg<Integer> indexArgument;
    private final RequiredArg<String> nameArgument;
    private final RequiredArg<String> valueArgument;

    public ExampleCommand(String name, String description) {
        super(name, description);
        this.indexArgument = withRequiredArg("index", "Index", ArgTypes.INTEGER);
        this.nameArgument = withRequiredArg("name", "Name", ArgTypes.STRING);
        this.valueArgument = withRequiredArg("value", "Value", ArgTypes.STRING);
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        int index = context.get(indexArgument);
        String name = context.get(nameArgument);
        String value = context.get(valueArgument);
        context.sendMessage(Message.translation("topper.line")
                .param("index", index)
                .param("name", name)
                .param("value", value)
        );
        return CompletableFuture.completedFuture(null);
    }

}

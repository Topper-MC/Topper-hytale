package me.hsgamer.topper.hytale.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.*;
import it.unimi.dsi.fastutil.Pair;
import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.hytale.TopperPlugin;
import me.hsgamer.topper.query.display.number.NumberDisplay;
import me.hsgamer.topper.template.topplayernumber.holder.NumberTopHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GetTopListCommand extends AbstractCommand {
    private final RequiredArg<NumberTopHolder> holderContext;
    private final DefaultArg<Pair<Integer, Integer>> rangeContext;

    public GetTopListCommand(TopperPlugin plugin) {
        super("gettoplist", "Get Top List");
        this.holderContext = withRequiredArg("holder", "The Top Holder this command will look for", new SingleArgumentType<>("Holder Name", "A string representing the name of the Top Holder") {
            @Override
            public @Nullable NumberTopHolder parse(String key, ParseResult parseResult) {
                NumberTopHolder holder = plugin.getTopTemplate().getTopManager().getHolder(key).orElse(null);
                if (holder == null) {
                    parseResult.fail(Message.parse("Holder not found").color(Color.RED));
                    return null;
                }
                return holder;
            }
        });
        this.rangeContext = withDefaultArg("range", "The range of the indexes in the list", new MultiArgumentType<>("Integer Range", "Two positive integers representing a minimum and maximum of a range", new String[]{"1 10", "5 5", "1 5"}) {
            private final WrappedArgumentType<Integer> minValue = withParameter("min", "Minimum value, must be less than or equal to max value", ArgTypes.INTEGER);
            private final WrappedArgumentType<Integer> maxValue = withParameter("max", "Maximum value, must be greater than or equal to min value", ArgTypes.INTEGER);

            @Override
            public Pair<Integer, Integer> parse(@Nonnull MultiArgumentContext context, @Nonnull ParseResult parseResult) {
                Integer min = context.get(minValue);
                Integer max = context.get(maxValue);
                assert min != null && max != null;
                if (min <= 0) {
                    parseResult.fail(Message.parse("Min value must be a positive number"));
                    return null;
                } else if (min > max) {
                    parseResult.fail(Message.raw("You cannot set the minimum value as larger than the maximum value. Min: " + min + " Max: " + max));
                    return null;
                } else {
                    return Pair.of(context.get(this.minValue), context.get(this.maxValue));
                }
            }
        }, Pair.of(1, 10), "The list starting from 1 to 10");
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NotNull CommandContext context) {
        NumberTopHolder holder = holderContext.get(context);
        NumberDisplay<UUID, Double> display = holder.getValueDisplay();
        SnapshotAgent<UUID, Double> snapshot = holder.getSnapshotAgent();

        Pair<Integer, Integer> range = rangeContext.get(context);
        boolean hasEntry = false;
        for (int i = range.left(); i <= range.right(); i++) {
            Map.Entry<UUID, Double> entry = snapshot.getSnapshotByIndex(i - 1).orElse(null);
            String name = display.getDisplayName(entry == null ? null : entry.getKey());
            String key = display.getDisplayKey(entry == null ? null : entry.getKey());
            String value = display.getDisplayValue(entry == null ? null : entry.getValue(), "");
            context.sendMessage(Message.translation("topper.line")
                    .param("index", Integer.toString(i))
                    .param("key", key)
                    .param("name", name)
                    .param("value", value)
            );
            hasEntry = true;
        }
        if (!hasEntry) {
            context.sendMessage(Message.translation("topper.empty"));
        }
        return CompletableFuture.completedFuture(null);
    }
}

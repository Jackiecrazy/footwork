package jackiecrazy.footwork.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import jackiecrazy.footwork.capability.timeslow.TimeSlowData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class SteveTimeCommand {

    public static final SimpleCommandExceptionType MISSING_ARGUMENT = new SimpleCommandExceptionType(Component.translatable("footwork.command.missing"));

    public static int missingArgument(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        throw MISSING_ARGUMENT.create();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("stevetime")
                .requires(s -> s.hasPermission(2))
                .executes(SteveTimeCommand::missingArgument)
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(SteveTimeCommand::missingArgument)
                        .then(Commands.argument("duration", IntegerArgumentType.integer(0))
                                .executes(SteveTimeCommand::missingArgument)
                                .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0))
                                        .executes(SteveTimeCommand::slow)
                                )
                        )
                );
        dispatcher.register(builder);
    }

    private static int slow(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity player = ctx.getSource().getPlayer();
        if (player == null) throw EntitySelectorOptions.ERROR_INAPPLICABLE_OPTION.create("sender");
        double speed = DoubleArgumentType.getDouble(ctx, "speed");
        int dur = IntegerArgumentType.getInteger(ctx, "duration");
        final Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        for(Entity e: targets){
            TimeSlowData.getCap(e).alterSpeed(dur, speed);
        }
        if (targets.size() == 1) {
            ctx.getSource().sendSuccess(() -> {
                return Component.translatable("footwork.command.slow.single", targets.iterator().next().getDisplayName());
            }, true);
        } else {
            ctx.getSource().sendSuccess(() -> {
                return Component.translatable("footwork.command.slow.multiple", targets.size());
            }, true);
        }
        return targets.size();
    }


}
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

public class UnRemoveCommand {

    public static final SimpleCommandExceptionType MISSING_ARGUMENT = new SimpleCommandExceptionType(Component.translatable("footwork.command.missing"));

    public static int missingArgument(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        throw MISSING_ARGUMENT.create();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("unremove")
                .requires(s -> s.hasPermission(2))
                .executes(UnRemoveCommand::missingArgument)
                .then(Commands.argument("targets", EntityArgument.player())
                        .executes(UnRemoveCommand::unremove)
                );
        dispatcher.register(builder);
    }

    private static int unremove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity player = ctx.getSource().getPlayer();
        if (player == null) throw EntitySelectorOptions.ERROR_INAPPLICABLE_OPTION.create("sender");
        player.revive();
        return 0;
    }


}
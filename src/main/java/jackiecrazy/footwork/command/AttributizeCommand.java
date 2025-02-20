package jackiecrazy.footwork.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class AttributizeCommand {

    public static final SimpleCommandExceptionType MISSING_ARGUMENT = new SimpleCommandExceptionType(Component.translatable("footwork.command.missing"));
    private static final double[] spread = {
            0.2,
            0.3,
            0.3,
            0.2
    };
    private static final String[] vals = {
            "max_fracture",
            "max_posture",
            "posture_regen",
            "evasion",
            "stealth"
    };
    private static JsonParser parser = new JsonParser();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static int missingArgument(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        throw MISSING_ARGUMENT.create();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("attributize")
                .requires(s -> s.hasPermission(2))
                .executes(AttributizeCommand::missingArgument)
                .then(Commands.argument("max_fracture", DoubleArgumentType.doubleArg(0))
                        .executes(AttributizeCommand::missingArgument)
                        .then(Commands.argument("max_posture", DoubleArgumentType.doubleArg(0))
                                .executes(AttributizeCommand::missingArgument)
                                .then(Commands.argument("posture_regen", DoubleArgumentType.doubleArg(0))
                                        .executes(AttributizeCommand::missingArgument)
                                        .then(Commands.argument("evasion", DoubleArgumentType.doubleArg(0))
                                                .executes(AttributizeCommand::missingArgument)
                                                .then(Commands.argument("stealth", DoubleArgumentType.doubleArg(-100, 0))
                                                        .executes(AttributizeCommand::missingArgument)
                                                        .then(Commands.argument("as", StringArgumentType.string())
                                                                .executes(AttributizeCommand::attributize)
                                                        )
                                                )
                                        )
                                )
                        )
                );
        dispatcher.register(builder);
    }

    private static int attributize(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity player = ctx.getSource().getPlayer();
        if (player == null) throw EntitySelectorOptions.ERROR_INAPPLICABLE_OPTION.create("sender");
        String str = StringArgumentType.getString(ctx, "as");
        File path = new File("attributized" + File.separatorChar + str + ".json");
        StringBuilder uglyJson = new StringBuilder("{");
        boolean s = false;
        int index = -1;
        for (ItemStack is : player.getArmorSlots()) {
            index++;
            if (is.isEmpty()) continue;
            double mult = spread[index];
            if (s) uglyJson.append(",");
            s = true;
            uglyJson.append("\"").append(ForgeRegistries.ITEMS.getKey(is.getItem())).append("\":[");
            boolean started = false;
            for (String key : vals) {
                double val = DoubleArgumentType.getDouble(ctx, key)*mult;
                if (val == 0) continue;
                if (started)
                    uglyJson.append("      ,");
                else started = true;
                uglyJson.append("      {");
                uglyJson.append("        \"attribute\":\"footwork:" + key + "\",");
                uglyJson.append("        \"modify\":" + Math.round(val*10)/10d+",");
                uglyJson.append("        \"operation\":\"ADDITION\"");
                uglyJson.append("      }");
            }
            uglyJson.append("]");
        }
        uglyJson.append("}");
        BufferedWriter writer = null;
        try {
            path.getParentFile().mkdirs();
            path.createNewFile();
            writer = new BufferedWriter(new FileWriter(path, true));
            JsonElement je = JsonParser.parseString(uglyJson.toString());
            String prettyJsonString = gson.toJson(je);
            writer.write(prettyJsonString);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw EntitySelectorOptions.ERROR_UNKNOWN_OPTION.create("something went wrong");
        }

        ctx.getSource().sendSuccess(()->Component.translatable("footwork.command.exported", str), false);
        return Command.SINGLE_SUCCESS;
    }


}
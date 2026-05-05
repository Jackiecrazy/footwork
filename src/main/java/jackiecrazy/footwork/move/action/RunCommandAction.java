package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RunCommandAction extends Action {
    private Argument<Entity> target = TargetEntityArgument.INSTANCE;
    private String command;

    @Override
    public int perform(ActionContext actionContext) {
        Entity target = this.target.resolve(actionContext);
        if (!StringUtil.isNullOrEmpty(command) && target != null) {
            try {
                final Level level = target.level();
                MinecraftServer minecraftserver = level.getServer();
                CommandSourceStack commandsourcestack = new CommandSourceStack(target, target.position(), target.getRotationVector(), level instanceof ServerLevel s ? s : null, 3, target.getName().getString(), target.getDisplayName(), level.getServer(), target).withSuppressedOutput();
                minecraftserver.getCommands().performPrefixedCommand(commandsourcestack, command);
            } catch (Throwable ignored) {
                return 0;
            }
        }
        return 0;
    }
}

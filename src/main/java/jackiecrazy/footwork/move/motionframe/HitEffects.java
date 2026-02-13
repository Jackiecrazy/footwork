package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HitEffects {
    public String command = "";
    public Vec3 velocity = Vec3.ZERO;
    public boolean set_velocity = false;
    public List<String> tags=new ArrayList<>();
    public List<Action> run_actions = new ArrayList<>();

    public HitEffects() {
    }

    public List<Action> run_actions() {
        return run_actions;
    }

    public boolean set_velocity() {
        return set_velocity;
    }

    public Vec3 velocity() {
        return velocity;
    }

    public String command() {
        return command;
    }

    public HitEffects copy() {
        HitEffects he = new HitEffects();
        he.run_actions = run_actions;
        he.command = command;
        he.velocity = velocity;
        he.set_velocity = set_velocity;
        return he;
    }

    public boolean runEffects(LivingEntity hitter, LivingEntity target) {
        Level level = target.level();
        if (!level.isClientSide) {
            MovementUtils.applyVelocity(velocity, target, set_velocity);
            if (!run_actions.isEmpty())
                ActionData.getCap(target).mark(hitter, new TimerActionsWrapper(run_actions));//todo check if this works
            MinecraftServer minecraftserver = level.getServer();
            for(String s:tags){
                CombatData.getCap(target).tickProc(s);
            }
            String command = this.command();
            if (!StringUtil.isNullOrEmpty(command)) {
                try {
                    CommandSourceStack commandsourcestack = new CommandSourceStack(target, target.position(), target.getRotationVector(), level instanceof ServerLevel s ? s : null, 3, target.getName().getString(), target.getDisplayName(), level.getServer(), target).withSuppressedOutput();
                    minecraftserver.getCommands().performPrefixedCommand(commandsourcestack, command);
                } catch (Throwable ignored) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}

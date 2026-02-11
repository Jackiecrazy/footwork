package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HitInfo {
    public static final HitInfo THROWN = new HitInfo(0, 1, 1, true, false, 1);
    public static final HitInfo BREACH = new HitInfo(0, 1, 1, true, true, 2);

    public double knockback = 1;
    public double damage_scale = 1;
    public double posture_scale = 1;
    public boolean crit = false;
    public boolean breach = false;
    public double crit_damage = 1.5;
    protected Vec3 knockback_direction = new Vec3(0, 0.2, 0.98);
    protected HitEffects hit_self = new HitEffects();
    protected HitEffects damage_self = new HitEffects();
    protected HitEffects hit_other = new HitEffects();
    protected HitEffects damage_other = new HitEffects();

    public HitInfo() {
    }

    public HitInfo(double knockback,
                   double damage_scale,
                   double posture_scale,
                   boolean crit,
                   boolean breach,
                   double crit_damage) {
        this.knockback = knockback;
        this.damage_scale = damage_scale;
        this.posture_scale = posture_scale;
        this.crit = crit;
        this.breach = breach;
        this.crit_damage = crit_damage;
    }

    public HitEffects hit_self() {
        return hit_self;
    }

    public HitEffects damage_self() {
        return damage_self;
    }

    public HitEffects hit_target() {
        return hit_other;
    }

    public HitEffects damage_target() {
        return damage_other;
    }

    public Vec3 knockback_direction() {
        return knockback_direction;
    }


    public HitInfo copyTo(HitInfo ret) {
        ret.knockback = knockback;
        ret.damage_scale = damage_scale;
        ret.posture_scale = posture_scale;
        ret.crit = crit;
        ret.crit_damage = crit_damage;
        ret.hit_self = hit_self.copy();
        ret.damage_self = damage_self.copy();
        ret.hit_other = hit_other.copy();
        ret.damage_other = damage_other.copy();
        return ret;
    }

    public void write(FriendlyByteBuf f) {
        f.writeDouble(knockback);
        f.writeDouble(damage_scale);
        f.writeDouble(posture_scale);
        f.writeBoolean(crit);
        f.writeDouble(crit_damage);
    }

    public void read(FriendlyByteBuf f) {
        knockback = f.readDouble();
        damage_scale = f.readDouble();
        posture_scale = f.readDouble();
        crit = f.readBoolean();
        crit_damage = f.readDouble();
    }

    public boolean runEffects(LivingEntity hitter, LivingEntity target, boolean self, boolean damage) {

        HitEffects he = self ? (damage ? damage_self : hit_self) : (damage ? damage_other : hit_other);
        Level level = target.level();
        if (!level.isClientSide) {
            MovementUtils.applyVelocity(he.velocity(), target, he.set_velocity());
            ActionData.getCap(target).mark(hitter, new TimerActionsWrapper(he.run_actions));//todo check if this works
            MinecraftServer minecraftserver = level.getServer();
            String command = he.command();
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

    public boolean canBreach() {
        return breach;
    }

    public double getKnockback() {
        return knockback;
    }

    public double getDamageScale() {
        return damage_scale;
    }

    public double getPostureScale() {
        return posture_scale;
    }

    public boolean isCrit() {
        return crit;
    }

    public double getCritDamage() {
        return crit_damage;
    }
}

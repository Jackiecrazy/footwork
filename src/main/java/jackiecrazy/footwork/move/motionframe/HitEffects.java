package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.CallbackActionWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.MovementUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HitEffects {
    public String command = "";
    public Vec3 velocity = Vec3.ZERO;
    public boolean set_velocity = false;
    public List<String> tags = new ArrayList<>();
    public List<Action> run_actions = new ArrayList<>();
    protected int guard_frames = 0;
    protected int dodge_frames = 0;
    protected int parry_frames = 0;
    protected int invulnerable_frames = 0;
    protected ResourceLocation sound = null;
    protected HitEffects on_guard;
    protected HitEffects on_parry;
    protected HitEffects on_dodge;
    protected HitEffects on_iframe;
    protected int stall = 0;
    private transient SoundEvent resolve = null;

    public HitEffects() {
    }

    public HitEffects setCommand(String command) {
        this.command = command;
        return this;
    }

    public HitEffects setVelocity(Vec3 velocity) {
        this.velocity = velocity;
        return this;
    }

    public HitEffects setSetVelocity(boolean set_velocity) {
        this.set_velocity = set_velocity;
        return this;
    }

    public HitEffects setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public HitEffects setRun_actions(List<Action> run_actions) {
        this.run_actions = run_actions;
        return this;
    }

    public HitEffects setGuard_frames(int guard_frames) {
        this.guard_frames = guard_frames;
        return this;
    }

    public HitEffects setDodge_frames(int dodge_frames) {
        this.dodge_frames = dodge_frames;
        return this;
    }

    public HitEffects setParry_frames(int parry_frames) {
        this.parry_frames = parry_frames;
        return this;
    }

    public HitEffects setInvulnerable_frames(int invulnerable_frames) {
        this.invulnerable_frames = invulnerable_frames;
        return this;
    }

    public HitEffects setSound(ResourceLocation sound) {
        this.sound = sound;
        return this;
    }

    public HitEffects setOn_guard(HitEffects on_guard) {
        this.on_guard = on_guard;
        return this;
    }

    public HitEffects setOn_parry(HitEffects on_parry) {
        this.on_parry = on_parry;
        return this;
    }

    public HitEffects setOn_dodge(HitEffects on_dodge) {
        this.on_dodge = on_dodge;
        return this;
    }

    public HitEffects setOn_iframe(HitEffects on_iframe) {
        this.on_iframe = on_iframe;
        return this;
    }

    public HitEffects on_guard() {
        return on_guard;
    }

    public HitEffects on_parry() {
        return on_parry;
    }

    public HitEffects on_dodge() {
        return on_dodge;
    }

    public HitEffects on_iframe() {
        return on_iframe;
    }

    public int guard_frames() {
        return guard_frames;
    }

    public int dodge_frames() {
        return dodge_frames;
    }

    public int parry_frames() {
        return parry_frames;
    }

    public int invulnerable_frames() {
        return invulnerable_frames;
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

    public boolean runEffects(LivingEntity hitter, Entity target, InteractionHand hand, ItemStack stack) {
        Level level = target.level();
        if (!level.isClientSide) {
            if (sound != null) {
                if (resolve == null)
                    resolve = SoundEvent.createVariableRangeEvent(sound);
                if (resolve != null) {
                    ServerLevel sl = (ServerLevel) target.level();
                    Vec3 pos = target.position();
                    sl.playSound(null, pos.x, pos.y, pos.z, resolve, SoundSource.PLAYERS, 0.8f + Footwork.rand.nextFloat() * 0.4f, 0.8f + Footwork.rand.nextFloat() * 0.4f);
                }
            }
            if (target instanceof LivingEntity le) {
                HitEffectRegistry.applyAll(this, hitter, target, hand, stack);
                final ICombatCapability cap = CombatData.getCap(le);
                if (!cap.alreadyProc(this.toString())) {
                    MovementUtils.applyVelocity(velocity, target, set_velocity);
                    cap.tickProc(this.toString());
                }
                for (String s : tags) {
                    cap.tickProc(s);
                }
                //todo is that right?
                if (guard_frames() > 0) cap.setGuardTime(guard_frames());
                if (parry_frames() > 0) cap.setParryTime(parry_frames());
                if (dodge_frames() > 0) cap.setDodgeTime(dodge_frames());
                if (invulnerable_frames() > 0) cap.setIframe(invulnerable_frames());

                if (on_guard != null) {
                    int time = cap.getGuardTime();
                    ActionData.getCap(target).mark(hitter, new CallbackActionWrapper.HitEffectCAW("guard", time, on_guard).addContext("itemstack", stack).addContext("hand", hand));
                }
                if (on_parry != null) {
                    int time = cap.getParryTime();
                    ActionData.getCap(target).mark(hitter, new CallbackActionWrapper.HitEffectCAW("parry", time, on_parry).addContext("itemstack", stack).addContext("hand", hand));
                }
                if (on_dodge != null) {
                    int time = cap.getDodgeTime();
                    ActionData.getCap(target).mark(hitter, new CallbackActionWrapper.HitEffectCAW("dodge", time, on_dodge).addContext("itemstack", stack).addContext("hand", hand));
                }
                if (on_iframe != null) {
                    int time = cap.getIframe();
                    ActionData.getCap(target).mark(hitter, new CallbackActionWrapper.HitEffectCAW("invul", time, on_iframe).addContext("itemstack", stack).addContext("hand", hand));
                }
            }
            if (!run_actions.isEmpty()) {
                final ActionSetWrapper wrapper = new ActionSetWrapper(run_actions);
                wrapper.addContext("hand", hand);
                wrapper.addContext("offhand", hand == InteractionHand.OFF_HAND);
                wrapper.addContext("itemstack", stack);
                ActionData.getCap(target).mark(hitter, wrapper);
            }
            MinecraftServer minecraftserver = level.getServer();
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

    // API
    public interface HitEffectBehavior {
        /**
         * Called at the end of (or during) HitEffect.execute().
         */
        void apply(HitEffects effect, LivingEntity hitter, Entity target, InteractionHand hand, ItemStack stack);
    }

//    @Deprecated //what the fuck
//    public boolean runEffects(LivingEntity hitter, LivingEntity target){
//        return runEffects(hitter, (Entity)target);
//    }

    public final class HitEffectRegistry {
        private static final List<HitEffectBehavior> BEHAVIORS = new CopyOnWriteArrayList<>();

        public static void register(HitEffectBehavior behavior) {
            BEHAVIORS.add(behavior);
        }

        static void applyAll(HitEffects effect,
                             LivingEntity hitter,
                             Entity target,
                             InteractionHand hand, ItemStack stack) {
            for (HitEffectBehavior b : BEHAVIORS) {
                b.apply(effect, hitter, target, hand, stack);
            }
        }
    }
}

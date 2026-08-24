package jackiecrazy.footwork.move.motionframe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HitInfo {
    public static final HitInfo THROWN = new HitInfo(0, 1, 1, true, false, 1).setSpirit_multiplier(0);
    public static final HitInfo BREACH = new HitInfo(0, 1, 1, true, true, 2).setSpirit_multiplier(0);
    public double knockback = 1;
    public double damage_scale = 1;
    public double posture_scale = 1;
    public boolean crit = false;
    public boolean breach = false;
    public double crit_damage = 1.5;
    public double spirit_multiplier = 1;
    public double armor_pierce = 0;
    protected Vec3 knockback_direction = null;
    public List<String> damage_tags = new ArrayList<>();
    protected HitEffects hit_self = new HitEffects();
    protected HitEffects damage_self = new HitEffects();
    protected HitEffects hit_other = new HitEffects();
    protected HitEffects damage_other = new HitEffects();
    //actually these are shoved in here so sweep attack and on_swing can proc them
    // for on_x effects, write them to a common buffer and track how long they should last?
    // or maybe write them down as marks with callbacks on defense actions, so they're technically a mark?
    // that solves holding for longer, but it can't handle early expiry...
    protected DragInfo drag = null;
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

    public DragInfo getDrag() {
        return drag;
    }

    public HitInfo withArmorPierce(double armor_pierce) {
        this.armor_pierce = armor_pierce;
        return this;
    }

    public HitInfo withKBDir(Vec3 knockback_direction) {
        this.knockback_direction = knockback_direction;
        return this;
    }

    public HitInfo withDamageTags(String... damage_tags) {
        this.damage_tags = List.of(damage_tags);
        return this;
    }

    public HitInfo withHitEffects(HitEffects fx, boolean damage, boolean other) {
        if(damage){
            if(other){
                damage_other=fx;
            }else{
                damage_self=fx;
            }
        }else{
            if(other){
                hit_other=fx;
            }else{
                hit_self=fx;
            }
        }
        return this;
    }

    public HitInfo withDrag(DragInfo drag) {
        this.drag = drag;
        return this;
    }

    public HitInfo setSpirit_multiplier(double spirit_multiplier) {
        this.spirit_multiplier = spirit_multiplier;
        return this;
    }

    public double spirit_multiplier() {
        return spirit_multiplier;
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
        ret.knockback_direction = knockback_direction;
        ret.breach = breach;
        return ret;
    }

    public void write(FriendlyByteBuf f) {
        f.writeDouble(knockback);
        f.writeDouble(damage_scale);
        f.writeDouble(posture_scale);
        f.writeBoolean(crit);
        f.writeDouble(crit_damage);
        f.writeDouble(spirit_multiplier);
        f.writeDouble(armor_pierce);
    }

    public void read(FriendlyByteBuf f) {
        knockback = f.readDouble();
        damage_scale = f.readDouble();
        posture_scale = f.readDouble();
        crit = f.readBoolean();
        crit_damage = f.readDouble();
        spirit_multiplier = f.readDouble();
        armor_pierce = f.readDouble();
    }

//    public boolean runEffects(LivingEntity hitter, LivingEntity target, boolean self, boolean damage) {
//        return runEffects(hitter, (Entity) target, self, damage);
//    }

    public boolean runEffects(LivingEntity hitter, Entity target, boolean self, boolean damage, InteractionHand hand, ItemStack stack) {

        HitEffects he = self ? (damage ? damage_self : hit_self) : (damage ? damage_other : hit_other);
        return he.runEffects(hitter, target, hand, stack);
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

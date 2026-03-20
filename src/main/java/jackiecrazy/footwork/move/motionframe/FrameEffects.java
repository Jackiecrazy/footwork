package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEffect;
import net.minecraft.world.phys.Vec3;

import java.util.List;

//Not synced to the client!
public class FrameEffects {
    private HitInfo attack_info = null;
    private List<FlyingWeaponEffect> effects = null;
    private double range = -1;
    private HitEffects perform_effects=null;
    private Vec3 velocity = Vec3.ZERO;
    private boolean set_velocity = false;

    public FrameEffects resetHit(boolean reset_hit) {
        this.reset_hit = reset_hit;
        return this;
    }

    public boolean reset_hit() {
        return reset_hit;
    }

    private boolean reset_hit=false;

    public FrameEffects() {

    }

//    public FrameEffects clone() {
//        FrameEffects ret = new FrameEffects();
//        ret.hit = hit;
//        ret.effects = effects;
//        ret.range = range;
//        ret.velocity = velocity;
//        ret.setVelocity = setVelocity;
//        return ret;
//    }

    public HitInfo getHit() {
        return attack_info;
    }

    public FrameEffects setHit(HitInfo attack_info) {
        this.attack_info = attack_info;
        return this;
    }

    public List<FlyingWeaponEffect> getEffects() {
        return effects;
    }

    public FrameEffects setEffects(FlyingWeaponEffect... effects) {
        this.effects = List.of(effects);
        return this;
    }

    public double getRange() {
        return range;
    }

    public FrameEffects setRange(double range) {
        this.range = range;
        return this;
    }

    public Vec3 getVelocity() {
        return velocity;
    }

    public FrameEffects setVelocity(Vec3 velocity) {
        this.velocity = velocity;
        return this;
    }

    public boolean isSetVelocity() {
        return set_velocity;
    }

    public FrameEffects setSetVelocity(boolean setVelocity) {
        this.set_velocity = setVelocity;
        return this;
    }
}

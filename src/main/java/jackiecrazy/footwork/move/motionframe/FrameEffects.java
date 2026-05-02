package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEffect;
import net.minecraft.world.phys.Vec3;

import java.util.List;

//Not synced to the client!
public class FrameEffects extends HitEffects {
    private HitInfo attack_info = null;
    private List<FlyingWeaponEffect> effects = null;
    private double range = -1;
    private boolean reset_hit = false;
    private boolean unDrag = false;

    public FrameEffects() {

    }

    public FrameEffects resetHit(boolean reset_hit) {
        this.reset_hit = reset_hit;
        return this;
    }

    public boolean reset_hit() {
        return reset_hit;
    }

    public HitEffects copy() {
        FrameEffects he = new FrameEffects();
        he.run_actions = run_actions;
        he.command = command;
        he.velocity = velocity;
        he.set_velocity = set_velocity;
        he.attack_info = attack_info;
        he.reset_hit = reset_hit;
        if (effects != null)
            he.setEffects(effects.toArray(new FlyingWeaponEffect[0]));
        he.setRange(range);
        return he;
    }

    public HitInfo getHit() {
        return attack_info;
    }

    public FrameEffects setHit(HitInfo attack_info) {
        this.attack_info = attack_info;
        return this;
    }

    public FrameEffects copyWithHit(HitInfo attack_info) {
        FrameEffects fe = (FrameEffects) copy();
        fe.attack_info = attack_info;
        return fe;
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

    public boolean shouldUndrag() {
        return unDrag;
    }
}

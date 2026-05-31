package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEffect;
import jackiecrazy.footwork.move.argument.misc.RenderItemArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

//Not synced to the client!
public class FrameEffects extends HitEffects {
    public static EntityDataSerializer<Color> COLOR = new EntityDataSerializer<>(){

        @Override
        public void write(FriendlyByteBuf b, Color c) {
            b.writeInt(c.getRed());
            b.writeInt(c.getGreen());
            b.writeInt(c.getBlue());
            b.writeInt(c.getAlpha());
        }

        @Override
        public Color read(FriendlyByteBuf b) {
            return new Color(b.readInt(), b.readInt(),b.readInt(), b.readInt());
        }

        @Override
        public Color copy(Color c) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        }
    };
    private HitInfo attack_info = null;
    private List<FlyingWeaponEffect> effects = null;
    private RenderItemArgument display_stack = null;
    private double range = -1;
    private boolean reset_hit = false;
    private boolean unDrag = false;
    private HitEffects on_terrain_impact=null;

    public HitEffects getTerrainEffects() {
        return on_terrain_impact;
    }

    private Color trail_color =new Color(0.6f, 0.8f, 1.0f);

    public Vec3 modify_initial_rotation() {
        return modify_initial_rotation;
    }

    private Vec3 modify_initial_rotation=new Vec3(1, 1, 1);

    public FrameEffects() {

    }

    public RenderItemArgument getDisplayItems() {
        return display_stack;
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
        if (display_stack != null)
            he.display_stack = display_stack;
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

    public Color getColor() {
        return trail_color;
    }
}

package jackiecrazy.footwork.move.motionframe;

import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HitEffects {
    public String command = "";
    public Vec3 velocity = Vec3.ZERO;
    public boolean set_velocity = false;
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
    public HitEffects copy(){
        HitEffects he=new HitEffects();
        he.run_actions=run_actions;
        he.command=command;
        he.velocity=velocity;
        he.set_velocity=set_velocity;
        return he;
    }
}

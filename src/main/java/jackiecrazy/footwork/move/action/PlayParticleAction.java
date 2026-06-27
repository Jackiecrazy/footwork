package jackiecrazy.footwork.move.action;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.ContextualVectorArgument;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
import jackiecrazy.footwork.move.utils.ActionContext;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayParticleAction extends Action {
    private Argument<ResourceLocation> particle;
    private transient ParticleType play;
    private String particle_parameters = "";
    private Condition seen_by_player = TrueCondition.INSTANCE;
    private Condition force = FalseCondition.INSTANCE;
    private Argument<Vec3> position = ContextualVectorArgument.INSTANCE, delta = RawVectorArgument.ZERO, velocity = RawVectorArgument.ZERO;
    private Argument<Double> quantity = FixedNumberArgument.ZERO;

    @Override
    public int perform(ActionContext actionContext) {
        if (play == null)
            play = ForgeRegistries.PARTICLE_TYPES.getValue(particle.resolve(actionContext));
        //type/data, force, pos xyz, quantity, vel xyz, max speed
        ParticleOptions p;
        Vec3 pos = position.resolve(actionContext);
        Vec3 delta = this.delta.resolve(actionContext);
        Vec3 speed = this.velocity.resolve(actionContext);
        if (play == null || pos == null || delta == null || speed == null) return 0;
        try {
            p = play.getDeserializer().fromCommand(play, new StringReader(" " + particle_parameters));
        } catch (CommandSyntaxException cse) {
            throw new RuntimeException(cse);
        }
        if (actionContext.target().level() instanceof ServerLevel sl) {
            for (ServerPlayer sp : sl.players()) {
                ActionContext ac = actionContext.wrapper().generateContext(actionContext.performer(), sp, actionContext.parent());
                if (seen_by_player.resolve(ac)) {
                    if (speed.lengthSqr() == 0)
                        sl.sendParticles(sp, p, force.resolve(actionContext), pos.x, pos.y, pos.z, quantity.resolve(actionContext).intValue(), delta.x, delta.y, delta.z, delta.length());
                    else {
                        double s=speed.length();
                        for (int x = -1; x < quantity.resolve(actionContext).intValue(); x++) {//using this impl to support velocity...
                            sl.sendParticles(sp, p, force.resolve(actionContext), pos.x + delta.x * Footwork.rand.nextDouble() * 2 - 1, pos.y + delta.y * Footwork.rand.nextDouble() * 2 - 1, pos.z + delta.z * Footwork.rand.nextDouble() * 2 - 1, 0, speed.x, speed.y, speed.z, s);
                        }
                    }
                }
            }
        }
        return 0;
    }
    //position, type, direction if any
}

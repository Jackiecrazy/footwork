package jackiecrazy.footwork.move.action;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.vector.ContextualVectorArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.PositionVectorArgument;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.condition.TrueCondition;
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
    private Argument<Vec3> position = ContextualVectorArgument.INSTANCE, direction = RawVectorArgument.ZERO;
    private Argument<Double> quantity = FixedNumberArgument.ZERO;

    @Override
    public int perform(ActionContext actionContext) {
        if (play == null)
            play = ForgeRegistries.PARTICLE_TYPES.getValue(particle.resolve(actionContext));
        if (play == null) return 0;
        //type/data, force, pos xyz, quantity, vel xyz, max speed
        ParticleOptions p;
        Vec3 pos = position.resolve(actionContext);
        Vec3 dir = direction.resolve(actionContext);
        try {
            p = play.getDeserializer().fromCommand(play, new StringReader(" " + particle_parameters));
        } catch (CommandSyntaxException cse) {
            throw new RuntimeException(cse);
        }
        if (actionContext.target().level() instanceof ServerLevel sl) {
            for (ServerPlayer sp : sl.players()) {
                ActionContext ac= new ActionContext(actionContext.wrapper(), actionContext.parent(), actionContext.performer(), sp);
                if (seen_by_player.resolve(ac)) {
                    sl.sendParticles(sp, p, force.resolve(actionContext), pos.x, pos.y, pos.z, (int) quantity.resolve(actionContext).intValue(), dir.x, dir.y, dir.z, dir.length());
                }
            }
        }
        return 0;
    }
    //position, type, direction if any
}

package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.vector.ContextualVectorArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.PositionVectorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class PlaySoundAction extends Action {
    private Argument<ResourceLocation> sound;
    private transient SoundEvent play;
    private SoundSource source = SoundSource.HOSTILE;
    private Argument<Vec3> position = ContextualVectorArgument.INSTANCE;
    private Argument<Double> volume = FixedNumberArgument.ONE;
    private Argument<Double> pitch = FixedNumberArgument.ONE;

    @Override
    public int perform(ActionContext actionContext) {
        if (play == null)
            play = ForgeRegistries.SOUND_EVENTS.getValue(sound.resolve(actionContext));
        if (play == null) return 0;
        //type/data, force, pos xyz, quantity, vel xyz, max speed
        Vec3 pos = position.resolve(actionContext);
        actionContext.performer().level().playSound(null, pos.x, pos.y, pos.z, play, source, volume.resolve(actionContext).floatValue(), pitch.resolve(actionContext).floatValue());
        return 0;
    }
    //position, type, direction if any
}

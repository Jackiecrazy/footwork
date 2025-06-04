package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.Moves;
import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.utils.JsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AttachActionAction extends Action {
    private Argument<Entity> performer = CasterEntityArgument.INSTANCE;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private Argument<ResourceLocation> effect;

    @Override
    public int perform(MovesetWrapper wrapper, Action parent, @Nullable Entity perform, Entity target) {
        ArrayList<TimerAction> timers = new ArrayList<>(List.of(JsonAdapters.gson.fromJson(Moves.moves.get(effect.resolve(wrapper, parent, perform, target)), TimerAction[].class)));
        ActionData.getCap(recipient.resolve(wrapper, parent, perform, target)).mark(performer.resolve(wrapper, parent, perform, target), new MovesetWrapper(timers));
        return 0;
    }
}

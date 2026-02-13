package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AttachActionAction extends Action {
    private Argument<Entity> performer = CasterEntityArgument.INSTANCE;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private Argument<ResourceLocation> effect;
    private transient ArrayList<Action> cachedEffect=null;

    @Override
    public int perform(TimerActionsWrapper wrapper, Action parent, @Nullable Entity perform, Entity target) {
        if(cachedEffect==null) cachedEffect= new ArrayList<>(List.of(ActionJsonAdapters.gson.fromJson(ActionSets.moves.get(effect.resolve(wrapper, parent, perform, target)), Action[].class)));
        ActionData.getCap(recipient.resolve(wrapper, parent, perform, target)).mark(performer.resolve(wrapper, parent, perform, target), new TimerActionsWrapper(cachedEffect));
        return 0;
    }
}

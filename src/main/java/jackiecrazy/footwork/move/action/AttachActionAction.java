package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AttachActionAction extends Action {
    private Argument<Entity> performer = CasterEntityArgument.INSTANCE;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private Argument<ResourceLocation> effect;
    private transient ArrayList<Action> cachedEffect=null;

    @Override
    public int perform(ActionContext actionContext) {
        if(cachedEffect==null) cachedEffect= new ArrayList<>(List.of(ActionJsonAdapters.gson.fromJson(ActionSets.moves.get(effect.resolve(actionContext)), Action[].class)));
        ActionData.getCap(recipient.resolve(actionContext)).mark(performer.resolve(actionContext), new ActionSetWrapper(cachedEffect));
        return 0;
    }
}

package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
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
    private ArrayList<Action> effect_list = null;

    @Override
    public int perform(ActionContext actionContext) {
        if (effect_list == null)
            effect_list = new ArrayList<>(List.of(ActionJsonAdapters.gson.fromJson(ActionSets.moves.get(effect.resolve(actionContext)), Action[].class)));
        if (effect_list == null){
            Footwork.LOGGER.warn("attempted to attach an invalid list of actions, skipping.");
            return 0;
        }
        ActionData.getCap(recipient.resolve(actionContext)).mark(performer.resolve(actionContext), new ActionSetWrapper(effect_list).appendContext(actionContext));
        return 0;
    }
}

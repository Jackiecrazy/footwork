package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.CallbackActionWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class AttachCallbackAction extends Action {
    private Argument<Entity> performer = CasterEntityArgument.INSTANCE;
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private Argument<ResourceLocation> effect = (a)->null;
    private ArrayList<Action> effect_list = null;
    private String trigger;
    private Argument<Double> duration;
    private Argument<Double> max_procs= FixedNumberArgument.ONE;

    @Override
    public int perform(ActionContext actionContext) {
        ResourceLocation rl = effect.resolve(actionContext);
        if (effect_list == null) {
            if (rl != null)
                effect_list = new ArrayList<>(List.of(ActionJsonAdapters.gson.fromJson(ActionSets.moves.get(rl), Action[].class)));
        }
        if (effect_list == null) {
            Footwork.LOGGER.error("attempted to attach an invalid list of callbacks, skipping.");
            return 0;
        }
        ActionData.getCap(recipient.resolve(actionContext))
                .mark(performer.resolve(actionContext), new CallbackActionWrapper(trigger, duration.resolve(actionContext).intValue(), effect_list).setMaxProcs(max_procs.resolve(actionContext).intValue()).setNamespace(rl).appendContext(actionContext));
        return 0;
    }
}

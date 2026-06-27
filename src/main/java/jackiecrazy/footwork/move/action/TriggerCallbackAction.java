package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.action.ActionData;
import jackiecrazy.footwork.move.ActionSets;
import jackiecrazy.footwork.move.CallbackActionWrapper;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TriggerCallbackAction extends Action {
    private Argument<Entity> recipient = TargetEntityArgument.INSTANCE;
    private String trigger="";
    private HashMap<String, Argument<?>> contexts=new HashMap<>();

    @Override
    public int perform(ActionContext actionContext) {
        final Entity tgt = recipient.resolve(actionContext);
        if(tgt==null)return 0;
        ActionContext ctx = new ActionContext(actionContext.wrapper(), this, actionContext.performer(), tgt);
        contexts.forEach((a,b)->ctx.addContext(a, b.resolve(actionContext)));
        ActionData.getCap(tgt).triggerCallback(trigger, ctx);
        return 0;
    }
}

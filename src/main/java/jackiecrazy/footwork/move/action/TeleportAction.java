package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TeleportAction extends Action {
    private Argument<Entity> subject= CasterEntityArgument.INSTANCE;
    private List<Action> on_start=new ArrayList<>();
    private List<Action> on_land=new ArrayList<>();
    private Argument<Vec3> position;

    @Override
    public int perform(ActionContext actionContext) {
        Vec3 vec=position.resolve(actionContext);
        Entity teleporter = subject.resolve(actionContext);
        runActions(new ActionContext(actionContext.wrapper(), actionContext.parent(), actionContext.performer(), teleporter).copyContextFrom(actionContext), on_start);
        teleporter.teleportTo(vec.x,vec.y, vec.z);
        runActions(new ActionContext(actionContext.wrapper(), actionContext.parent(), actionContext.performer(), teleporter).copyContextFrom(actionContext), on_land);
        return 0;
    }
}

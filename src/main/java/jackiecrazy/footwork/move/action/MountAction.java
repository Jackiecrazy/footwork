package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.entity.TargetEntityArgument;
import net.minecraft.world.entity.Entity;

public class MountAction extends Action{
    private Argument<Entity> mounter = CasterEntityArgument.INSTANCE;
    private Argument<Entity> mount = TargetEntityArgument.INSTANCE;
    @Override
    public int perform(ActionContext actionContext) {
        mounter.resolve(actionContext).startRiding(mount.resolve(actionContext), true);
        return 0;
    }
}

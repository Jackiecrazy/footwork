package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import net.minecraft.world.entity.Entity;

public class CasterEntityArgument extends EntityArgument{
    public static final CasterEntityArgument INSTANCE=new CasterEntityArgument();
    @Override
    public Entity resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return caster;
    }
}

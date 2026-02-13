package jackiecrazy.footwork.move.argument.entity;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class SelectorEntityArgument extends EntityArgument{
    private Argument<List<Entity>> select;
    @Override
    public Entity resolve(ArgumentContext argumentContext) {
        return select.resolve(argumentContext).get(0);
    }
}

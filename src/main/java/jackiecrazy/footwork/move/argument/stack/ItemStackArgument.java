package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackArgument implements Argument<ItemStack> {
    public abstract ItemStack resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target);
}

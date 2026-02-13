package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackArgument implements Argument<ItemStack> {
    public abstract ItemStack resolve(ArgumentContext argumentContext);
}

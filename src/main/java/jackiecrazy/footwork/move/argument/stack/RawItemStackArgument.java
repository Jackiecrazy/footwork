package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class RawItemStackArgument implements Argument<ItemStack>{
    private Argument<ResourceLocation> item;
    private Argument<Double> count = FixedNumberArgument.ZERO;
    private CompoundTag tag;

    public ItemStack resolve(ArgumentContext argumentContext) {
        ItemStack ret = new ItemStack(ForgeRegistries.ITEMS.getValue(item.resolve(argumentContext)));
        ret.setCount((int) Math.max(count.resolve(argumentContext), 1));
        if (tag != null)
            ret.setTag(tag.copy());
        return ret;
    }
}

package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TagItemArgument implements Argument<ItemStack> {
    private Argument<ResourceLocation> tag;
    private transient TagKey<Item> me;
    private transient List<Item> resolved = new ArrayList<>();

    @Override
    public @Nullable ItemStack resolve(ArgumentContext argumentContext) {
        if (me == null || !me.location().equals(tag.resolve(argumentContext))) {
            me = ItemTags.create(tag.resolve(argumentContext));
        }
        if (me != null) {
            if (resolved.isEmpty()) {
                resolved = BuiltInRegistries.ITEM.stream()
                        .filter(item -> item.builtInRegistryHolder().is(me))
                        .toList();
            }
            if (!resolved.isEmpty()) {
                return new ItemStack(resolved.get(Footwork.rand.nextInt(resolved.size())));
            }
        }
        return ItemStack.EMPTY;
    }
}

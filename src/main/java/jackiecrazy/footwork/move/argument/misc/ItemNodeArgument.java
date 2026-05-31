package jackiecrazy.footwork.move.argument.misc;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ItemNodeArgument implements Argument<RenderNode.ItemNode> {
    public Argument<ItemStack> getStack() {
        return stack;
    }

    public ItemNodeArgument setStack(Argument<ItemStack> stack) {
        this.stack = stack;
        return this;
    }

    private Argument<ItemStack> stack;
    //rotations cannot be raw vectors which is regressive.
    private Vec3 rotation= Vec3.ZERO, translation= Vec3.ZERO;
    @Override
    public @Nullable RenderNode.ItemNode resolve(ArgumentContext ctx) {
        return new RenderNode.ItemNode(stack.resolve(ctx), rotation, translation);
    }
}

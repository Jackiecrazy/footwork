package jackiecrazy.footwork.move.argument.misc;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.move.condition.FalseCondition;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RenderNodeArgument implements Argument<RenderNode> {
    private Argument<ItemStack> stack;

    public BlockState getBlockstate() {
        return blockstate;
    }

    private BlockState blockstate;
    //rotations cannot be raw vectors which is regressive.
    private Vec3 rotation = Vec3.ZERO, translation = Vec3.ZERO;
    private Condition force_item = FalseCondition.INSTANCE;
    private transient RenderNode cached = null;

    public Argument<ItemStack> getStack() {
        return stack;
    }

    public RenderNodeArgument setBlockstate(BlockState blockstate) {
        this.blockstate = blockstate;
        return this;
    }

    public RenderNodeArgument setStack(Argument<ItemStack> stack) {
        this.stack = stack;
        return this;
    }

    @Override
    public @Nullable RenderNode resolve(ArgumentContext ctx) {
        if (cached != null) return cached;
        if (blockstate != null) {
            cached = new RenderNode.BlockNode(blockstate, rotation, translation);
            return cached;
        } else if (stack != null){
            final ItemStack is = stack.resolve(ctx);
            if(!force_item.resolve(ctx)&&is.getItem() instanceof BlockItem bi) {
                return new RenderNode.BlockNode(bi.getBlock().defaultBlockState(), rotation, translation);
            }
            return new RenderNode.ItemNode(is, rotation, translation);
        }
        else {
            cached = new RenderNode.BaseItemNode(rotation, translation);
            return cached;
        }
    }
}

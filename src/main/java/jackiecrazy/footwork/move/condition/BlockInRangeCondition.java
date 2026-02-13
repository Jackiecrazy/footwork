package jackiecrazy.footwork.move.condition;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.PositionVectorArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInRangeCondition extends Condition {
    private Argument<ResourceLocation> block;
    private Condition testAsTag;
    private transient Block bloc;
    private transient TagKey<Block> tag;
    private Argument<Double> range, min_count = FixedNumberArgument.ZERO;
    private Argument<Vec3> from = PositionVectorArgument.CASTER;

    @Override
    public Boolean resolve(ArgumentContext argumentContext) {
        boolean testTag=testAsTag.resolve(argumentContext);
        if (testTag) {
            if (tag == null)
                tag = new TagKey<>(ForgeRegistries.BLOCKS.getRegistryKey(), block.resolve(argumentContext));
        } else if (bloc == null)
            bloc = ForgeRegistries.BLOCKS.getValue(block.resolve(argumentContext));
        Vec3 pos = from.resolve(argumentContext);
        double radius = range.resolve(argumentContext);
        return min_count.resolve(argumentContext) <= argumentContext.performer().level().getBlockStatesIfLoaded(new AABB(pos, pos).inflate(radius)).filter(bs -> testTag ? bs.is(tag) : bs.is(bloc)).count();
    }
}

package jackiecrazy.footwork.move.motionframe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record ItemNode(ItemStack stack, Vec3 rotate, Vec3 translate) {
}

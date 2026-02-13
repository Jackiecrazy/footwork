package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EquippedItemArgument implements Argument<ItemStack> {
    private Argument<Entity> wielder = CasterEntityArgument.INSTANCE;
    private EquipmentSlot slot=EquipmentSlot.MAINHAND;

    @Override
    public ItemStack resolve(ArgumentContext argumentContext) {
        return wielder.resolve(argumentContext) instanceof LivingEntity e ? e.getItemBySlot(slot) : ItemStack.EMPTY;
    }
}

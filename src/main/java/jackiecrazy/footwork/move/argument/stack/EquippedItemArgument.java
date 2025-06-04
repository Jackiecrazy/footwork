package jackiecrazy.footwork.move.argument.stack;

import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EquippedItemArgument implements Argument<ItemStack> {
    private Argument<Entity> wielder = CasterEntityArgument.INSTANCE;
    private EquipmentSlot slot=EquipmentSlot.MAINHAND;

    @Override
    public ItemStack resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target) {
        return wielder.resolve(wrapper, parent, caster, target) instanceof LivingEntity e ? e.getItemBySlot(slot) : ItemStack.EMPTY;
    }
}

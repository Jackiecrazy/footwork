package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SetItemAction extends Action {
    private Argument<Entity> wielder = CasterEntityArgument.INSTANCE;
    private Argument<ItemStack> stack;
    private EquipmentSlot slot;

    @Override
    public int perform(ActionContext actionContext) {
        if (wielder.resolve(actionContext) instanceof LivingEntity e)
            e.setItemSlot(slot, stack.resolve(actionContext).copy());
        return 0;
    }
}

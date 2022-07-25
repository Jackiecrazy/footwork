package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.api.WarAttributes;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    @SubscribeEvent
    public static void takeThis(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof MobEntity) {
            MobEntity mob = (MobEntity) e.getEntity();
            mob.goalSelector.addGoal(-1, new NoGoal(mob));
            mob.targetSelector.addGoal(-1, new NoGoal(mob));
            if (e.getEntity() instanceof CreatureEntity) {
                CreatureEntity creature = (CreatureEntity) e.getEntity();
                mob.targetSelector.addGoal(0, new CompelledVengeanceGoal(creature));
                mob.targetSelector.addGoal(0, new FearGoal(creature));
            }
        }
    }

    @SubscribeEvent
    public static void tickMobs(LivingEvent.LivingUpdateEvent e) {
        LivingEntity elb = e.getEntityLiving();
        if (!elb.level.isClientSide) {
            if (elb.hasEffect(FootworkEffects.PETRIFY.get())) {
                elb.xRot = elb.xRotO;
                elb.yRot = elb.yRotO;
                elb.yHeadRot = elb.yHeadRotO;
                elb.setDeltaMovement(0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void noJump(LivingEvent.LivingJumpEvent e) {
        if (!(e.getEntityLiving() instanceof PlayerEntity) && CombatData.getCap(e.getEntityLiving()).getStaggerTime() > 0) {
            e.getEntityLiving().setDeltaMovement(0, 0, 0);
        }
    }

}

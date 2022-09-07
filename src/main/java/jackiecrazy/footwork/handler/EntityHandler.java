package jackiecrazy.footwork.handler;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.entity.ai.CompelledVengeanceGoal;
import jackiecrazy.footwork.entity.ai.FearGoal;
import jackiecrazy.footwork.entity.ai.NoGoal;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Footwork.MODID)
public class EntityHandler {

    @SubscribeEvent
    public static void takeThis(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof Mob) {
            Mob mob = (Mob) e.getEntity();
            mob.goalSelector.addGoal(-1, new NoGoal(mob));
            mob.targetSelector.addGoal(-1, new NoGoal(mob));
            if (e.getEntity() instanceof PathfinderMob) {
                PathfinderMob creature = (PathfinderMob) e.getEntity();
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
                elb.setXRot(elb.xRotO);
                elb.setYRot(elb.yRotO);
                elb.yHeadRot = elb.yHeadRotO;
                elb.setDeltaMovement(0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void noJump(LivingEvent.LivingJumpEvent e) {
        if (!(e.getEntityLiving() instanceof Player) && CombatData.getCap(e.getEntityLiving()).getStaggerTime() > 0) {
            e.getEntityLiving().setDeltaMovement(0, 0, 0);
        }
    }

}

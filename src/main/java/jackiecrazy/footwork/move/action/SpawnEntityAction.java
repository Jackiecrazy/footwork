package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.vector.ContextualVectorArgument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.entity.CasterEntityArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.vector.LookVectorArgument;
import jackiecrazy.footwork.move.argument.vector.PositionVectorArgument;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import jackiecrazy.footwork.utils.GeneralUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SpawnEntityAction extends Action {
    private Argument<Entity> summoner = CasterEntityArgument.INSTANCE;
    private Argument<ResourceLocation> entity;
    private CompoundTag tag;
    private Argument<Double> quantity = FixedNumberArgument.ONE;
    private Argument<Double> spread = FixedNumberArgument.ZERO;
    private Argument<Vec3> position = ContextualVectorArgument.INSTANCE;
    private Argument<Vec3> facing = new LookVectorArgument();
    private Argument<Vec3> velocity = RawVectorArgument.ZERO;
    private List<Action> on_spawn = new ArrayList<>();

    @Override
    public int perform(ActionContext actionContext) {
        Entity summoner = this.summoner.resolve(actionContext);
        int toSpawn = quantity.resolve(actionContext).intValue();
        double deviation = spread.resolve(actionContext);
        Vec3 vec = position.resolve(actionContext);
        Level level = actionContext.performer().level();
        if (level instanceof ServerLevel serverlevel) {
            for (int x = 0; x < toSpawn; x++) {
                Vec3 rand = new Vec3((Footwork.rand.nextDouble()) * deviation, (Footwork.rand.nextDouble()) * deviation, (Footwork.rand.nextDouble()) * deviation);
                final Vec3 pos = vec.add(rand);
                CompoundTag compoundtag = tag == null ? new CompoundTag() : tag.copy();
                compoundtag.putString("id", entity.resolve(actionContext).toString());
                Vec3 look = facing.resolve(actionContext);
                Entity summon = EntityType.loadEntityRecursive(compoundtag, serverlevel, (toSummon) -> {
                    Vec3 velocity = this.velocity.resolve(actionContext);
                    toSummon.setDeltaMovement(velocity);
                    double flatDist = Math.sqrt(look.x * look.x + look.z * look.z);
                    toSummon.moveTo(pos.x, pos.y, pos.z, (float) Mth.wrapDegrees(GeneralUtils.deg((float) Mth.atan2(look.z, look.x)) - 90.0F), Mth.wrapDegrees(-GeneralUtils.deg((float) Mth.atan2(look.y, flatDist))));
                    if (toSummon instanceof Projectile p) {
                        p.setOwner(summoner);
                        p.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.length(), (float) deviation);
                    }
                    if (toSummon instanceof TamableAnimal p) {
                        p.setOwnerUUID(summoner.getUUID());
                    }
                    return toSummon;
                });
                if (summon != null) {
                    if (tag == null && summon instanceof Mob m) {
                        m.finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(summon.blockPosition()), MobSpawnType.COMMAND, null, null);
                    }

                    serverlevel.tryAddFreshEntityWithPassengers(summon);
                    for (Action a : on_spawn) {
                        a.perform(new ActionContext(actionContext.wrapper(), actionContext.parent(), summoner, summon).copyContextFrom(actionContext));
                    }

                }
            }
        }
        return 0;
    }
}

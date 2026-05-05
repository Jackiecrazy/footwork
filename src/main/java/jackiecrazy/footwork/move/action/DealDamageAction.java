package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.move.argument.DamageArgument;
import jackiecrazy.footwork.api.CombatDamageSource;
import net.minecraft.world.damagesource.DamageSource;

import java.util.ArrayList;
import java.util.List;

public class DealDamageAction extends Action {

    private Argument<Double> amount;
    private DamageArgument damage_source;

    private List<Action> on_hit = new ArrayList<>();
    private List<Action> on_damage = new ArrayList<>();
    private List<Action> on_kill = new ArrayList<>();

    @Override
    public int perform(ActionContext actionContext) {
        DamageSource baked = damage_source.resolve(actionContext);
        boolean success = actionContext.target().hurt(baked, amount.resolve(actionContext).floatValue());
        if (baked instanceof CombatDamageSource cds)
            actionContext.performer().getPersistentData().putDouble("footwork:finalized_damage", cds.getFinalDamage());
        int ret = runActions(actionContext, on_hit);
        if (success) {
            if (!actionContext.target().isAlive()) {
                int damageRet = runActions(actionContext, on_kill);
                if (damageRet != 0) ret = damageRet;
            }
            int damageRet = runActions(actionContext, on_damage);
            if (damageRet != 0) ret = damageRet;
        }
        actionContext.performer().getPersistentData().remove("footwork:finalized_damage");
        return ret;
    }
}

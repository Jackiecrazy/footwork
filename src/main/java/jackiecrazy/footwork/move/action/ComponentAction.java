package jackiecrazy.footwork.move.action;

import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.argument.Argument;
import net.minecraft.resources.ResourceLocation;

public class ComponentAction extends TimerAction {
    private Argument<ResourceLocation> component;
    private transient TimerAction referent;


}

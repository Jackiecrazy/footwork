package jackiecrazy.footwork.capability.action;

import jackiecrazy.footwork.move.TimerActionsWrapper;
import net.minecraft.world.entity.Entity;

public interface IAttachAction {
//    void setMovesetManager(MovesetManager mm);
//    MovesetManager getMovesetManager();
    void mark(Entity from, TimerActionsWrapper d);

    void update();
}

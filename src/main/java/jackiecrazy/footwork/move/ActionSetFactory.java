package jackiecrazy.footwork.move;

import com.google.gson.JsonParseException;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.utils.JsonAdapters;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ActionSetFactory {
    ResourceLocation move;
    transient ArrayList<TimerAction> finalized;

    public boolean validateAndBake() {
        if (finalized != null) return true;
        try {
            finalized = new ArrayList<>(List.of(JsonAdapters.gson.fromJson(ActionSets.moves.get(move), TimerAction[].class)));
        } catch (Exception e) {
            throw new JsonParseException("invalid action set" + move);
        }
        return true;
    }

    public ActionSetWrapper generateActionSet() {
        return new ActionSetWrapper(finalized);
    }
}

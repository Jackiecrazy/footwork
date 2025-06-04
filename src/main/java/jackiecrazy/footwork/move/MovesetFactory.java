package jackiecrazy.footwork.move;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.condition.Condition;
import jackiecrazy.footwork.utils.JsonAdapters;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MovesetFactory {
    int starting_weight = 1;
    int weight_change = 0;
    int size = 1;
    JsonObject condition;
    ResourceLocation move;
    transient ArrayList<TimerAction> finalized;
    transient Condition finalizedCondition;

    public boolean validateAndBake() {
        if (finalized != null && finalizedCondition != null) return true;
        try {
            finalized = new ArrayList<>(List.of(JsonAdapters.gson.fromJson(Moves.moves.get(move), TimerAction[].class)));
            finalizedCondition = JsonAdapters.gson.fromJson(condition, Condition.class);
        } catch (Exception e) {
            throw new JsonParseException("invalid moveset " + move + " or condition " + condition);
        }
        return true;
    }

    public MovesetWrapper generateMoveset() {
        return new MovesetWrapper(size, starting_weight, weight_change, finalized, finalizedCondition);
    }
}

package jackiecrazy.footwork.utils;

import com.google.gson.*;
import jackiecrazy.footwork.entity.flyingweapon.FlyingWeaponEffect;
import jackiecrazy.footwork.move.Macros;
import jackiecrazy.footwork.move.argument.number.OperateArgument;
import jackiecrazy.footwork.move.condition.ComparisonCondition;
import jackiecrazy.footwork.move.motionframe.FrameEffects;
import jackiecrazy.footwork.move.motionframe.HitInfo;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Locale;

public class JsonUtils {
    /**
     * Deeply merges source (delta/override) into target.
     * - Objects → recursive merge
     * - Arrays → merge by index (replace/override elements, don't concat)
     * - Primitives/strings/booleans → replace if present in source
     * - Null in source → remove field from target (optional behavior)
     */
    public static JsonObject deepMerge(JsonObject target, JsonObject source) {
        JsonObject result = target.deepCopy();  // work on copy to avoid mutating original

        for (String key : source.keySet()) {
            JsonElement sourceElem = source.get(key);

            if (!result.has(key)) {
                // New field → just add
                result.add(key, sourceElem.deepCopy());
                continue;
            }

            JsonElement targetElem = result.get(key);

            if (sourceElem.isJsonObject() && targetElem.isJsonObject()) {
                // Recursive object merge
                result.add(key, deepMerge(targetElem.getAsJsonObject(), sourceElem.getAsJsonObject()));
            } else if (sourceElem.isJsonArray() && targetElem.isJsonArray()) {
                // Merge arrays by index (common for your "frames")
                JsonArray targetArr = targetElem.getAsJsonArray();
                JsonArray sourceArr = sourceElem.getAsJsonArray();

                // Extend target if source is longer
                while (targetArr.size() < sourceArr.size()) {
                    targetArr.add(JsonNull.INSTANCE);  // placeholder, will be replaced
                }

                //merge it backwards
                for (int i = 0; i <sourceArr.size(); i++) {
                    JsonElement s = sourceArr.get(i);
                    if (s.isJsonObject() && targetArr.get(i).isJsonObject()) {
                        // Merge object at index iFramee
                        JsonObject merged = deepMerge(targetArr.get(i).getAsJsonObject(), s.getAsJsonObject());
                        targetArr.set(i, merged);
                    } else if (!s.isJsonNull()) {
                        // Replace primitive / entire sub-object
                        targetArr.set(i, s.deepCopy());
                    } else {
                        // remove if source has explicit null
                        targetArr.set(i, JsonNull.INSTANCE);
                    }
                }
                //find all null fields and eliminate them afterwards
                while(targetArr.remove(JsonNull.INSTANCE));
            } else {
                // Primitive / simple replacement (including explicit null)
                result.add(key, sourceElem.deepCopy());
            }
        }

        return result;
    }

    // Convenience: merge and convert back to your POJO if needed
    public static <T> T mergeIntoObject(T baseObj, JsonObject delta, Gson gson, Class<T> clazz) {
        JsonObject baseJson = gson.toJsonTree(baseObj).getAsJsonObject();
        JsonObject mergedJson = deepMerge(baseJson, delta);
        return gson.fromJson(mergedJson, clazz);
    }

    /**
     * Checks if the root object contains any HitInfo fields.
     * If yes → deserializes them + other FrameEffects fields into one object.
     * Returns null if no HitInfo-related fields were found.
     */
    static FrameEffects extractRootFrameEffects(JsonObject obj, JsonDeserializationContext context,
                                                boolean wipeVelocity) {
        String[] frameEffectFields = {
                "hit",
                "effects",
                "range",
                "velocity",
                "set_velocity",
                "command",
                "tags",
                "run_actions"
        };

        boolean hasAnyField = false;
        for (String field : frameEffectFields) {
            if (obj.has(field)) {
                hasAnyField = true;
                break;
            }
        }
        boolean hasHitInfo = false;

        // List of fields that belong to HitInfo
        String[] hitInfoFields = {
                "knockback",
                "damage_scale",
                "posture_scale",
                "crit",
                "breach",
                "crit_damage",
                "knockback_direction",
                "hit_self_command",
                "hit_other_command",
                "damage_self_command",
                "damage_other_command",
                "hit_self_velocity",
                "hit_self_set_velocity",
                "damage_self_velocity",
                "damage_self_set_velocity"
        };

        for (String field : hitInfoFields) {
            if (obj.has(field)) {
                hasHitInfo = true;
                break;
            }
        }

        if (!hasAnyField && !hasHitInfo) {
            return null;  // ← none present → no override / return null
        }

        FrameEffects ret = context.deserialize(obj, FrameEffects.class);

        if (wipeVelocity) {
            ret.setVelocity(Vec3.ZERO);
            ret.setSetVelocity(false);
            //undoing is necessary as it's handled by Animation
        }

        if (hasHitInfo && ret.getHit() == null) {
            // Still check if other FrameEffects fields exist
            ret.setHit(context.deserialize(obj, HitInfo.class));
        }
        if (ret.getEffects() == null) {
            ret.setEffects(FlyingWeaponEffect.WEAPON, FlyingWeaponEffect.TRAIL);
        }

        return ret;
    }

    public static JsonElement parseSyntacticSugar(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            if (obj.has("execute_macro")) {
                return Macros.expandMacroWithParams(obj);
            }

            if (obj.has("ID")) {
                String id = obj.get("ID").getAsString();
                //operations syntactic sugar
                final String upperCase = id.toUpperCase(Locale.ROOT);
                if (Arrays.stream(OperateArgument.OPERATOR.values()).anyMatch(a -> a.name().equals(upperCase))) {
                    OperateArgument.OPERATOR op = OperateArgument.OPERATOR.valueOf(upperCase);
                    obj.addProperty("operation", op.toString());
                    obj.addProperty("ID", "operate");
                }
                if (Arrays.stream(OperateArgument.OPERATOR.values()).anyMatch(a -> a.toString().equals(upperCase))) {
                    obj.addProperty("operation", upperCase);
                    obj.addProperty("ID", "operate");
                }

                //conditions syntactic sugar
                if (Arrays.stream(ComparisonCondition.COMPARISON.values()).anyMatch(a -> a.name().equals(upperCase))) {
                    ComparisonCondition.COMPARISON op = ComparisonCondition.COMPARISON.valueOf(upperCase);
                    obj.addProperty("comparison", op.toString());
                    obj.addProperty("ID", "compare_number");
                } else if (Arrays.stream(ComparisonCondition.COMPARISON.values()).anyMatch(a -> a.toString().equals(upperCase))) {
                    obj.addProperty("comparison", upperCase);
                    obj.addProperty("ID", "compare_number");
                }
            }

            //time window syntactic sugar
            if (obj.has("time_window")) {
                String time = obj.getAsJsonPrimitive("time_window").getAsString();
                int splitIndex = time.indexOf("-");
                if (splitIndex < 0) splitIndex = time.length() - 1;
                String from = time.substring(0, splitIndex);
                String to = time.substring(splitIndex + 1);
                obj.addProperty("ID", "time_window");
                if (!from.isEmpty())
                    obj.addProperty("from", Integer.valueOf(from));
                if (!to.isEmpty())
                    obj.addProperty("to", Integer.valueOf(to));
            }

            JsonObject copy = new JsonObject();
            for (var e : obj.entrySet()) {
                copy.add(e.getKey(), parseSyntacticSugar(e.getValue()));
            }
            return copy;
        }

        if (element.isJsonArray()) {
            JsonArray arr = new JsonArray();
            for (JsonElement e : element.getAsJsonArray()) {
                arr.add(parseSyntacticSugar(e));
            }
            return arr;
        }

        return element;
    }
}

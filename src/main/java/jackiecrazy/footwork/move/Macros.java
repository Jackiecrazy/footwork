package jackiecrazy.footwork.move;

import com.google.gson.*;
import jackiecrazy.footwork.move.argument.number.OperateArgument;
import jackiecrazy.footwork.move.condition.ComparisonCondition;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Macros extends SimpleJsonResourceReloadListener {
    public static final HashMap<ResourceLocation, JsonObject> map = new HashMap<>();

    public Macros() {
        super(ActionJsonAdapters.gson, "action_macros");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new Macros());
    }

    public static JsonElement parseSyntacticSugar(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            if (obj.has("execute_macro")) {
                return expandMacroWithParams(obj);
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

    private static JsonElement expandMacroWithParams(JsonObject call) {

        ResourceLocation id = new ResourceLocation(call.get("execute_macro").getAsString());
        JsonObject templateDef = map.get(id);

        if (templateDef == null)
            throw new JsonParseException("Unknown macro: " + id);

        JsonObject paramSchema = templateDef.getAsJsonObject("parameters");
        if (paramSchema == null) paramSchema = new JsonObject();

        Map<String, JsonElement> resolvedParams = resolveParameters(paramSchema, call);

        JsonElement templateCopy = templateDef.get("execute").deepCopy();

        JsonElement substituted = substituteParams(templateCopy, resolvedParams);

        // Recursively expand nested templates
        return parseSyntacticSugar(substituted);
    }

    private static Map<String, JsonElement> resolveParameters(JsonObject schema, JsonObject provided) {
        Map<String, JsonElement> result = new HashMap<>();

        for (var entry : schema.entrySet()) {
            String name = entry.getKey();
            JsonElement def = entry.getValue();

            JsonElement value = provided.get(name);

            if (value == null) {
                value = def;
            }

            result.put(name, value);
        }

        return result;
    }

    private static JsonElement substituteParams(JsonElement element, Map<String, JsonElement> params) {

        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String str = element.getAsString();

            if (str.startsWith("$")) {
                String key = str.substring(1);
                JsonElement replacement = params.get(key);

                if (replacement == null)
                    throw new JsonParseException("Unknown macro param: " + key);

                return replacement.deepCopy();
            }

            return element;
        }

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonObject copy = new JsonObject();

            for (var e : obj.entrySet()) {
                copy.add(e.getKey(), substituteParams(e.getValue(), params));
            }

            return copy;
        }

        if (element.isJsonArray()) {
            JsonArray arr = new JsonArray();
            for (JsonElement e : element.getAsJsonArray()) {
                arr.add(substituteParams(e, params));
            }
            return arr;
        }

        return element;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> obj,
                         ResourceManager manager,
                         ProfilerFiller filler) {
        map.clear();
        obj.forEach((a, b) -> {
            if (b.isJsonObject())
                map.put(a, b.getAsJsonObject());
        });
    }
}

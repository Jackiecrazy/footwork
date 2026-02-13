package jackiecrazy.footwork.move;

import com.google.gson.*;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import jackiecrazy.footwork.utils.JsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;

public class Macros extends SimpleJsonResourceReloadListener {
    public static final HashMap<ResourceLocation, JsonObject> map = new HashMap<>();

    public Macros() {
        super(ActionJsonAdapters.gson, "action_macros");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new Macros());
    }

    public static JsonElement expandMacroWithParams(JsonObject call) {

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
        return JsonUtils.parseSyntacticSugar(substituted);
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

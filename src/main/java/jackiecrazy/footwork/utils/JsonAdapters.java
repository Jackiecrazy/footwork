package jackiecrazy.footwork.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jackiecrazy.footwork.move.motionframe.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonAdapters {
    public static final Gson NAIVE = new GsonBuilder()
            .registerTypeAdapter(CompoundTag.class, new ActionJsonAdapters.NBTAdapter())
            .registerTypeAdapter(Vec3.class, new Vec3TypeAdapter())
            .setPrettyPrinting()
            .create();

    public static class HitInfoAdapter implements JsonDeserializer<HitInfo> {

        @Override
        public HitInfo deserialize(JsonElement json,
                                   Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
            final JsonObject obj = json.getAsJsonObject();
            for (String first : new String[]{"hit", "damage"}) {
                for (String second : new String[]{"_self", "_other"}) {
                    for (String third : new String[]{"command", "velocity", "set_velocity"}) {
                        final String joined = first + second + "_" + third;
                        if (obj.has(joined)) {
                            //add property if not there
                            final String key = first + second;
                            if (!obj.has(key))
                                obj.add(key, new JsonObject());
                            obj.get(key).getAsJsonObject().add(third, obj.get(joined));
                        }
                    }
                }
            }
            return ActionJsonAdapters.gson.fromJson(json, HitInfo.class);//todo check
        }
    }

    public static class Vec3TypeAdapter extends TypeAdapter<Vec3> {

        @Override
        public void write(JsonWriter out, Vec3 value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            out.value(value.x);
            out.value(value.y);
            out.value(value.z);
            out.endArray();
        }

        @Override
        public Vec3 read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            in.beginArray();
            double x = in.nextDouble();
            double y = in.nextDouble();
            double z = in.nextDouble();
            in.endArray();
            return new Vec3(x, y, z);
        }
    }

    public static class MotionFrameAdapter implements JsonDeserializer<MotionFrame> {

        @Override
        public MotionFrame deserialize(JsonElement json,
                                       Type typeOfT,
                                       JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException(json + " is not a json object");
            JsonObject o = json.getAsJsonObject();
            MotionFrame mf = NAIVE.fromJson(json, MotionFrame.class);
            assert mf.direction() != null;
            assert mf.offset() != null;
            if (mf.effects() == null)
                mf.setEffects(JsonUtils.extractRootFrameEffects(o, context, false));
            if (mf.renderOrientation() == null) {
                //try checking other fields: int rotation or vec4f dir+rot
                if (o.has("render_rotation")) {
                    JsonElement spinElem = o.get("render_rotation");
                    if (spinElem.isJsonPrimitive() && spinElem.getAsJsonPrimitive().isNumber()) {
                        // Scalar number → apply to X-axis only (common convention for yaw spin)
                        int scalar = spinElem.getAsInt();
                        mf = new MotionFrame(mf.direction(), mf.offset(), scalar).setEffects(mf.effects());

                    } else if (spinElem.isJsonArray()) {
                        // [x, y, z]
                        JsonArray arr = spinElem.getAsJsonArray();
                        if (arr.size() != 4) {
                            throw new JsonParseException("render_rotation array must have exactly 4 elements");
                        }
                        Vector4d spinVector = new Vector4d(
                                arr.get(0).getAsDouble(),
                                arr.get(1).getAsDouble(),
                                arr.get(2).getAsDouble(),
                                arr.get(3).getAsDouble()
                        );
                        if (spinVector.x == 0 && spinVector.y == 0 && spinVector.z == 0) {
                            throw new JsonParseException("render_rotation array must define a nonzero length vector with its xyz");
                        }
                        mf = new MotionFrame(mf.direction(), mf.offset(), spinVector).setEffects(mf.effects());

                    } else if (spinElem.isJsonObject()) {
                        // {"x":, "y":, "z":}
                        JsonObject spinObj = spinElem.getAsJsonObject();
                        Vector4d spinVector = new Vector4d(
                                spinObj.has("x") ? spinObj.get("x").getAsDouble() : 0,
                                spinObj.has("y") ? spinObj.get("y").getAsDouble() : 0,
                                spinObj.has("z") ? spinObj.get("z").getAsDouble() : 0,
                                spinObj.has("w") ? spinObj.get("w").getAsDouble() : 0
                        );

                        if (spinVector.x == 0 && spinVector.y == 0 && spinVector.z == 0) {
                            throw new JsonParseException("render_rotation array must define a nonzero length vector with its xyz");
                        }
                        mf = new MotionFrame(mf.direction(), mf.offset(), spinVector).setEffects(mf.effects());

                    } else {
                        throw new JsonParseException("'render_rotation' must be a number, array[4], or object{x,y,z,w}");
                    }
                } else if (!o.has("_skipOrientationCalculation"))
                    mf = new MotionFrame(mf.direction(), mf.offset()).setEffects(mf.effects());
            }
            return mf;
        }
    }

    public static class MotionManagerDeserializer implements JsonDeserializer<MotionManager> {


        private static double signedAngle(Vec3 a, Vec3 b, Vec3 edgeNormal) {
            Vec3 cross = a.cross(b);
            double dot = a.dot(b);
            double sign = edgeNormal.dot(cross);
            return Math.atan2(sign, dot);
        }
        @Override
        public MotionManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("MotionManager must be a JSON object");
            }

            JsonObject obj = json.getAsJsonObject();
            MotionManager ret;

            // ────────────────────────────────────────────────
            // Case 1: Has "frames" → DefinitionMM + MotionGroup
            // ────────────────────────────────────────────────
            if (obj.has("frames") && obj.get("frames").isJsonArray()) {
                JsonArray framesArray = obj.getAsJsonArray("frames");

                List<MotionFrame> frames = new ArrayList<>();
                boolean manuallyCalculate = framesArray.size() > 1;
                for (JsonElement elem : framesArray) {
                    if (manuallyCalculate) elem.getAsJsonObject().addProperty("_skipOrientationCalculation", true);
                    MotionFrame frame = context.deserialize(elem, MotionFrame.class);
                    frames.add(frame);
                }

                if (frames.isEmpty()) {
                    throw new JsonParseException("frames array cannot be empty for DefinitionMM");
                }

                //compute frames from bottom to top.
                if (manuallyCalculate) {
                    //todo dot products are inherently positive and so we can't extract the sign, what to do?
                    for (int x = frames.size() - 1; x >= 1; x--) {
                        //If they have no render orientation, set their orientation to their direction, and their angling to the angle of the frame change
                        final MotionFrame mf = frames.get(x);
                        if (mf.renderOrientation() != null) continue;
                        Vec3 endFrame = mf.direction();
                        Vec3 startFrame = frames.get(x - 1).direction();
                        final Vec3 down = new Vec3(0, -1, 0);
                        final Vec3 movement = endFrame.subtract(startFrame);
                        double angleRadians = signedAngle(down, movement, new Vec3(0,0,-1));
                        double angleDegrees = Math.toDegrees(angleRadians);
                        mf._setRenderRotationRaw(MotionFrame.buildLocalRotation(new Vector4d(endFrame.x, endFrame.y, endFrame.z, angleDegrees)));
                    }
                    //finally compute the first frame from the second frame
                    final MotionFrame mf = frames.get(0);
                    if (mf.renderOrientation() == null) {
                        Vec3 startFrame = mf.direction();
                        Vec3 endFrame = frames.get(1).direction();
                        final Vec3 down = new Vec3(0, -1, 0);
                        final Vec3 movement = endFrame.subtract(startFrame);
                        double angleRadians = signedAngle(down, movement, new Vec3(0,0,-1));
                        double angleDegrees = Math.toDegrees(angleRadians);
                        mf._setRenderRotationRaw(MotionFrame.buildLocalRotation(new Vector4d(startFrame.x, startFrame.y, startFrame.z, angleDegrees)));
                    }
                }

                // Extract MotionGroup fields (easing, duration)
                EasingFunctionEnum easing = obj.has("easing")
                        ? context.deserialize(obj.get("easing"), EasingFunctionEnum.class)
                        : EasingFunctionEnum.LINEAR;  // or your default

                int dur;
                if (obj.has("duration"))
                    dur = obj.get("duration").getAsInt();
                else
                    throw new JsonParseException("no duration defined for " + json);  // or 20 / throw if you want stricter


                MotionGroup group = new MotionGroup(frames, easing, dur);
                MotionManagers.DefinitionMM defMM = new MotionManagers.DefinitionMM(group);
                ret = defMM;
            } else {

                // ────────────────────────────────────────────────
                // Case 2: No "frames" → single MotionFrame → FixedMM
                // ────────────────────────────────────────────────
                // Deserialize the whole object as a MotionFrame
                MotionFrame singleFrame = context.deserialize(obj, MotionFrame.class);

                // Determine duration (fallback or from root)
                int singleDuration = 5;
                if (obj.has("duration"))
                    singleDuration = obj.get("duration").getAsInt();
                else
                    throw new JsonParseException("no duration defined for " + json);  // or 20 / throw if you want stricter

                MotionManagers.FixedMM fixed = new MotionManagers.FixedMM(singleFrame, singleDuration);
                ret = fixed;
            }
            applySpinIfPresent(obj, ret, context);
            return ret;
        }

        private void applySpinIfPresent(JsonObject obj, MotionManager targetFrame, JsonDeserializationContext context) {
            if (!obj.has("spin")) {
                return;
            }

            JsonElement spinElem = obj.get("spin");
            Vector3f spinVector;

            if (spinElem.isJsonPrimitive() && spinElem.getAsJsonPrimitive().isNumber()) {
                // Scalar number → apply to X-axis only (common convention for yaw spin)
                float scalar = spinElem.getAsFloat();
                spinVector = new Vector3f(scalar, 0, 0);

            } else if (spinElem.isJsonArray()) {
                // [x, y, z]
                JsonArray arr = spinElem.getAsJsonArray();
                if (arr.size() != 3) {
                    throw new JsonParseException("'spin' array must have exactly 3 elements");
                }
                spinVector = new Vector3f(
                        arr.get(0).getAsFloat(),
                        arr.get(1).getAsFloat(),
                        arr.get(2).getAsFloat()
                );

            } else if (spinElem.isJsonObject()) {
                // {"x":, "y":, "z":}
                JsonObject spinObj = spinElem.getAsJsonObject();
                spinVector = new Vector3f(
                        spinObj.has("x") ? spinObj.get("x").getAsFloat() : 0,
                        spinObj.has("y") ? spinObj.get("y").getAsFloat() : 0,
                        spinObj.has("z") ? spinObj.get("z").getAsFloat() : 0
                );

            } else {
                throw new JsonParseException("'spin' must be a number, array[3], or object{x,y,z}");
            }

            targetFrame.setAngularVelocity(spinVector.mul(Mth.DEG_TO_RAD));
        }

    }
}

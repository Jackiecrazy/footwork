package jackiecrazy.footwork.config;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;

public class DisplayConfigUtils {
    public enum AnchorPoint {
        TOPLEFT,
        TOPCENTER,
        TOPRIGHT,
        MIDDLELEFT,
        CROSSHAIR,
        MIDDLERIGHT,
        BOTTOMLEFT,
        BOTTOMCENTER,
        BOTTOMRIGHT
    }

    public static enum BarType {
        CLASSIC,
        AMO,
        DARKMEGA
    }

    public static Pair<Integer, Integer> translateCoords(DisplayData dd, int width, int height) {
        return translateCoords(dd.anchorPoint, dd.numberX, dd.numberY, width, height);
    }

    public static Pair<Integer, Integer> translateCoords(AnchorPoint ap, int x, int y, int width, int height) {
        int retx, rety;
        switch (ap) {
            case TOPLEFT:
                retx = 0;
                rety = 0;
                break;
            case TOPRIGHT:
                retx = 0;
                rety = width;
                break;
            case CROSSHAIR:
                retx = width / 2;
                rety = height / 2;
                break;
            case TOPCENTER:
                retx = width / 2;
                rety = 0;
                break;
            case BOTTOMLEFT:
                retx = 0;
                rety = height;
                break;
            case MIDDLELEFT:
                retx = 0;
                rety = height / 2;
                break;
            case BOTTOMRIGHT:
                retx = width;
                rety = height;
                break;
            case MIDDLERIGHT:
                retx = width;
                rety = height / 2;
                break;
            case BOTTOMCENTER:
                retx = width / 2;
                rety = height;
                break;
            default:
                retx = rety = 0;
        }
        retx = MathHelper.clamp(retx + x, 0, width);
        rety = MathHelper.clamp(rety + y, 0, height);
        return Pair.of(retx, rety);
    }

    public static class DisplayData {
        private final ForgeConfigSpec.EnumValue<AnchorPoint> _anchor;
        private final ForgeConfigSpec.IntValue _numberX;
        private final ForgeConfigSpec.IntValue _numberY;
        private final ForgeConfigSpec.BooleanValue _display;
        public AnchorPoint anchorPoint;
        public int numberX;
        public int numberY;
        public boolean enabled;

        public DisplayData(ForgeConfigSpec.Builder b, String s, AnchorPoint ap, int defX, int defY) {
            _display = b.translation("wardance.config." + s + "enabled").comment("enable displaying this feature").define("enable " + s, true);
            _anchor = b.translation("wardance.config." + s + "anchor").comment("the point from which offsets will calculate").defineEnum(s + " anchor point", ap);
            _numberX = b.translation("wardance.config." + s + "X").comment("where the center of the HUD element should be in relation to the anchor point").defineInRange(s + " x offset", defX, -Integer.MAX_VALUE, Integer.MAX_VALUE);
            _numberY = b.translation("wardance.config." + s + "Y").comment("where the center of the HUD element should be in relation to the anchor point").defineInRange(s + " y offset", defY, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        public void bake() {
            anchorPoint = _anchor.get();
            numberX = _numberX.get();
            numberY = _numberY.get();
            enabled = _display.get();
        }
    }
}

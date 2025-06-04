package jackiecrazy.footwork.api;

import net.minecraft.nbt.CompoundTag;

public class WallRunningData {
    public enum JUMPSTATE {
        GROUNDED,
        EXHAUSTED,
        JUMPING,
        DODGING,
        CLINGING
    }

    public static class ClingData {
        boolean n, s, e, w;

        public ClingData(boolean[] dirs) {
            this(dirs[5], dirs[4], dirs[0], dirs[1]);
        }

        public ClingData(boolean north, boolean south, boolean east, boolean west) {
            n = north;
            s = south;
            e = east;
            w = west;
        }

        public ClingData(CompoundTag nbt) {
            n = nbt.getBoolean("north");
            s = nbt.getBoolean("south");
            e = nbt.getBoolean("east");
            w = nbt.getBoolean("west");
        }

        public boolean north() {
            return n;
        }

        public boolean south() {
            return s;
        }

        public boolean east() {
            return e;
        }

        public boolean west() {
            return w;
        }

        public CompoundTag toNBT(CompoundTag nbt) {
            nbt.putBoolean("north", n);
            nbt.putBoolean("south", s);
            nbt.putBoolean("east", e);
            nbt.putBoolean("west", w);
            return nbt;
        }

        public boolean equals(Object o) {
            if (!(o instanceof ClingData)) return false;
            ClingData cd = (ClingData) o;
            return cd.e == e && cd.n == n && cd.s == s && cd.w == w;
        }
    }
}

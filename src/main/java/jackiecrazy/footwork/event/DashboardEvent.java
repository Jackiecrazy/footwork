package jackiecrazy.footwork.event;

import jackiecrazy.footwork.client.screen.dashboard.DashboardScreen;
import jackiecrazy.footwork.client.screen.dashboard.PonderingOrb;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;

public class DashboardEvent extends Event {
    private final ArrayList<PonderingOrb> thoughts = new ArrayList<>();
    private DashboardScreen base;
    private Player p;

    public DashboardEvent(Player p, DashboardScreen parent) {
        this.p = p;
        base = parent;
    }

    public ArrayList<PonderingOrb> getThoughts() {
        return thoughts;
    }

    public void addThought(PonderingOrb thought) {
        thoughts.add(thought);
    }

    public DashboardScreen getScreen() {
        return base;
    }

    public Player getPlayer() {
        return p;
    }
}

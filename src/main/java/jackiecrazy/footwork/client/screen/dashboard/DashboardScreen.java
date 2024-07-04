package jackiecrazy.footwork.client.screen.dashboard;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.event.DashboardEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DashboardScreen extends Screen {
    PonderingOrb clickOn = null;
    int disappearTime = 100;
    PonderingOrb focus = null;
    /*
    The dashboard screen is completely dark. Buttons are added by each mod to open their respective guis.

     */
    private Player p;
    private List<PonderingOrb> orbs = new ArrayList<>();

    public DashboardScreen(Player player) {
        super(Component.translatable("footwork.gui.dashboard"));
        p = player;
    }

    public void render(@Nonnull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (int i = 0; i < orbs.size(); i++) {
            PonderingOrb A = orbs.get(i);
            for (int j = i + 1; j < orbs.size(); j++) {
                PonderingOrb B = orbs.get(j);
                if (collides(A, B)) {
                    handleCollision(A, B);
                }
            }
        }
        //move focus to center after setting
        if (clickOn != null) {
            if (clickOn.getX() == width / 2f - clickOn.getWidth() / 2f || disappearTime-- < 0) {
                clickOn.yVelocity = clickOn.xVelocity = 0;
                clickOn.alpha -= 0.1f;
                if (clickOn.alpha <= 0)
                    clickOn.onClick(0, 0);
            } else clickOn.alpha = Math.min(1, clickOn.alpha + 0.05f);
        }
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int i = width / 2;
        int j = height + 100;
        matrixStack.pose().translate(0, 0, 100);
        InventoryScreen.renderEntityInInventoryFollowsMouse(matrixStack, i -25, j -35, i + 25, j + 35, 100, 0.0625F, mouseX, mouseY, this.minecraft.player);
        matrixStack.pose().translate(0, 0, -100);
    }

    @Override
    protected void init() {
        //collect all pondering orbs
        final DashboardEvent ds = new DashboardEvent(p, this);
        NeoForge.EVENT_BUS.post(ds);
        orbs = ds.getThoughts();
        //figure out how big each orb should be
        int orbSize = 64;
        while (orbSize > width || orbSize > height || orbSize * orbSize * orbs.size() > width * height) {
            orbSize /= 2;
        }
        //scatter orbs with random velocity
        for (PonderingOrb ponder : orbs) {
            int tries = 0;
            do {
                ponder.init(Footwork.rand.nextInt(width - orbSize), Footwork.rand.nextInt(height - orbSize), orbSize);
                tries++;
            } while (tries < 10 && orbs.stream().anyMatch(a -> a != ponder && collides(a, ponder)));
            addRenderableWidget(ponder);
        }
    }

    @Override
    public boolean mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        if (this.focus != null && clickOn == null) {
            //start moving the focus to the center
            float half = focus.getWidth() / 2f;
            focus.xVelocity = (width / 2f - half - (focus.getX())) / 50;
            focus.yVelocity = (height / 2f - half - (focus.getY())) / 50;
            focus.alpha = 1;
            clickOn = focus;
        }

        return false;
    }

    void handleCollision(PonderingOrb A, PonderingOrb B) {

        double xDist = A.getX() - B.getX();
        double yDist = A.getY() - B.getY();
        double distSquared = xDist * xDist + yDist * yDist;
        double xVelocity = B.xVelocity - A.xVelocity;
        double yVelocity = B.yVelocity - A.yVelocity;
        double dotProduct = xDist * xVelocity + yDist * yVelocity;
        //Neat vector maths, used for checking if the objects moves towards one another.
        if (dotProduct > 0) {
            double collisionScale = dotProduct / distSquared;
            double xCollision = xDist * collisionScale;
            double yCollision = yDist * collisionScale;
            //The Collision vector is the speed difference projected on the Dist vector,
            //thus it is the component of the speed difference needed for the collision.
            double aMass = A == focus ? 1 : 1000;
            double bMass = B == focus ? 1 : 1000;
            double combinedMass = aMass + bMass;
            double collisionWeightA = 2 * aMass / combinedMass;
            double collisionWeightB = 2 * bMass / combinedMass;
            A.xVelocity += collisionWeightA * xCollision;
            A.yVelocity += collisionWeightA * yCollision;
            B.xVelocity -= collisionWeightB * xCollision;
            B.yVelocity -= collisionWeightB * yCollision;
        }
    }

    boolean collides(PonderingOrb ball1, PonderingOrb ball2) {
        double dx = ball1.getX() - ball2.getX();
        double dy = ball1.getY() - ball2.getY();
        double distance = (dx * dx + dy * dy);
        if (distance <= (ball1.getWidth()) * (ball1.getHeight())) {
            return true;
        }
        return false;
    }
}

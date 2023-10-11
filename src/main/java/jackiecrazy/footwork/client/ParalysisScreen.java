package jackiecrazy.footwork.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParalysisScreen extends Screen {
    protected ParalysisScreen() {
        super(Component.empty());
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int p_96563_, int p_96564_, float p_96565_) {
        //nothing at all!
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics p_96557_) {
        //nope!
    }

    @Override
    public void renderDirtBackground(@NotNull GuiGraphics p_96559_) {
        //nope!
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return super.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener p_94677_) {
        super.setFocused(p_94677_);
    }
}

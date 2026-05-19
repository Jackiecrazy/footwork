package jackiecrazy.footwork.client.screen.dashboard;

import jackiecrazy.footwork.networking.AnimationTesterSavePacket;
import jackiecrazy.footwork.networking.FootworkChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import static jackiecrazy.footwork.items.AnimationTesterItem.*;

public class AnimationTesterScreen extends Screen {
    private final ItemStack stack;
    private EditBox renderInput;      // Upper big box
    private EditBox motionInput;      // Lower JSON box
    private Button saveButton;
    private Checkbox isManagerCheckbox;

    public AnimationTesterScreen(ItemStack stack) {
        super(Component.literal("Animation Tester"));
        this.stack = stack;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Upper box - RenderGroup / Item name
        renderInput = new EditBox(this.font, centerX - 200, centerY - 80, 400, 80, Component.literal("Render Group / Item"));
        renderInput.setMaxLength(8192);
        renderInput.setValue(getCurrentRenderData());
        this.addRenderableWidget(renderInput);

        // Checkbox
        // Instead of Checkbox.builder...
        isManagerCheckbox = new Checkbox(
                centerX - 200,           // x
                centerY - 5,             // y
                150,                     // width (adjust as needed)
                20,                      // height
                Component.literal("Is MotionManager"),
                stack.getOrCreateTag().getBoolean(NBT_IS_MANAGER)  // checked state
        );
        this.addRenderableWidget(isManagerCheckbox);

        // Lower box - JSON
        motionInput = new EditBox(this.font, centerX - 200, centerY + 20, 400, 80, Component.literal("Motion JSON"));
        motionInput.setMaxLength(16384);
        motionInput.setValue(stack.getOrCreateTag().getString(NBT_MOTION_DATA));
        this.addRenderableWidget(motionInput);

        // Save button
        saveButton = Button.builder(Component.literal("Save to Item"), btn -> saveData())
                .pos(centerX - 100, height - 20)
                .size(200, 20)
                .build();
        this.addRenderableWidget(saveButton);
    }

    private String getCurrentRenderData() {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_RENDER_GROUP)) {
            return tag.getString("inputted_item"); // or pretty print if you want
        }
        return "";
    }

    private void saveData() {
        if (Minecraft.getInstance().getConnection() == null) return;

        // Send to server
        AnimationTesterSavePacket packet = new AnimationTesterSavePacket(
                Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).equals(stack)?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND,
                renderInput.getValue(),
                motionInput.getValue(),
                isManagerCheckbox.selected()
        );
        FootworkChannel.INSTANCE.sendToServer(packet);

        this.minecraft.setScreen(null); // close
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(font, "Upper: Item registry name OR JSON array of ItemNode",
                                       width/2, height/2 - 110, 0xFFFFFF);
        guiGraphics.drawCenteredString(font, "Lower: MotionFrame or MotionManager JSON",
                                       width/2, height/2 + 5, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() { return true; }
}

package jackiecrazy.footwork.items;

import jackiecrazy.footwork.client.screen.dashboard.AnimationTesterScreen;
import jackiecrazy.footwork.entity.FootworkEntities;
import jackiecrazy.footwork.entity.flyingweapon.DummyFlyingWeaponEntity;
import jackiecrazy.footwork.move.motionframe.MotionFrame;
import jackiecrazy.footwork.move.motionframe.MotionManager;
import jackiecrazy.footwork.move.motionframe.MotionManagers;
import jackiecrazy.footwork.move.motionframe.render.RenderItemGroup;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AnimationTesterItem extends Item {
    public static final String NBT_RENDER_GROUP = "RenderGroup";
    public static final String NBT_MOTION_DATA = "MotionData"; // JSON string
    public static final String NBT_IS_MANAGER = "IsMotionManager"; // boolean

    public AnimationTesterItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                Minecraft.getInstance().setScreen(new AnimationTesterScreen(stack));
            }
            return InteractionResultHolder.success(stack);
        } else {
            // Normal right click -> spawn flying entity (your code)
            if (!level.isClientSide) {
                spawnFlyingWeapon(level, player, stack);
            }
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        entity.level().getEntities(entity, entity.getBoundingBox().inflate(32), a -> a instanceof DummyFlyingWeaponEntity)
                .forEach(a -> a.remove(Entity.RemovalReason.KILLED));
        return super.onEntitySwing(stack, entity);
    }

    private void spawnFlyingWeapon(Level level, Player player, ItemStack stack) {
        // Your existing entity spawning logic here
        // Pull RenderGroup + Motion data from stack.getOrCreateTag()
        // Example stub:
        CompoundTag tag = stack.getOrCreateTag();
        // ...
        DummyFlyingWeaponEntity entity = new DummyFlyingWeaponEntity(FootworkEntities.WEAPON.get(), level);
        if(tag.contains("stack"))
            entity.setHeldItem(ItemStack.of(tag.getCompound("stack")));
        entity.setCosmeticItem(RenderItemGroup.fromTag(tag.getCompound(NBT_RENDER_GROUP)));
        entity.setPosRaw(player.getX(), player.getY(), player.getZ());
        entity.setOwner(player);
        String stuff = tag.getString(NBT_MOTION_DATA);
        if (tag.getBoolean(NBT_IS_MANAGER)) {
            MotionManager mf = ActionJsonAdapters.gson.fromJson(stuff, MotionManager.class);
            if (mf != null)
                entity.setTesting(mf);
        } else {
            MotionFrame mf = ActionJsonAdapters.gson.fromJson(stuff, MotionFrame.class);
            if (mf != null)
                entity.setIdlePose(new MotionManagers.FixedMM(mf, 5));
        }
        level.addFreshEntity(entity);
    }
}
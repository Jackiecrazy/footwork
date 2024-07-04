package jackiecrazy.footwork.entity;

import jackiecrazy.footwork.Footwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class FootworkDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Footwork.MODID);

    public static final Supplier<AttachmentType<LivingEntity>> FEAR_TARGET = ATTACHMENT_TYPES.register(
            "fear_target", () -> AttachmentType.builder(() -> (LivingEntity)null).build()
    );
    public static final Supplier<AttachmentType<LivingEntity>> FORCE_TARGET = ATTACHMENT_TYPES.register(
            "forced_target", () -> AttachmentType.builder(() -> (LivingEntity)null).build()
    );
    public static final Supplier<AttachmentType<BlockPos>> SOUND_LOCATION = ATTACHMENT_TYPES.register(
            "sound_location", () -> AttachmentType.builder(() -> BlockPos.ZERO).build()
    );
}

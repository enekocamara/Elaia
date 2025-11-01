package net.enhalo.elaia.mixin;


import net.vulkanmod.vulkan.Vulkan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(net.vulkanmod.vulkan.Vulkan.class)

public abstract class VulkanExtensionMixin {
    @Unique
    private static final Set<String> extensions = Set.of(
            "VK_KHR_shader_float16_int8",
            "VK_KHR_16bit_storage"
    );
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onStaticInit(CallbackInfo ci) {
        Vulkan.REQUIRED_EXTENSION.addAll(extensions);
    }

    @Shadow @Final
    public static boolean ENABLE_VALIDATION_LAYERS = true;
}

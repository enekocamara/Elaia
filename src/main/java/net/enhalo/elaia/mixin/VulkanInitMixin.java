package net.enhalo.elaia.mixin;

import net.enhalo.elaia.vulkan.VulkanInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.vulkanmod.vulkan.Vulkan.class)
public abstract class VulkanInitMixin {
    @Inject(method = "initVulkan", at = @At("RETURN"))
    private static void initVulkan(CallbackInfo ci) {
        VulkanInitializer.signalInitialized();
    }
}
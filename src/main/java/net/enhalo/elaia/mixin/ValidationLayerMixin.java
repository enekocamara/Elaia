package net.enhalo.elaia.mixin;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(Vulkan.class)
public abstract class ValidationLayerMixin {

    @Shadow @Final
    public static boolean ENABLE_VALIDATION_LAYERS = VulkanConfig.ENABLE_VALIDATION_LAYERS;

    @ModifyVariable(
            method = "createInstance",
            at = @At(
                value = "STORE",
                ordinal = 0
            )
    )
    private static VkInstanceCreateInfo modifyCreateInfo(VkInstanceCreateInfo  createInfo) {
        if (Vulkan.ENABLE_VALIDATION_LAYERS){
            try (MemoryStack stack = MemoryStack.stackPush()) {
                // Add validation layer
                PointerBuffer layers = stack.mallocPointer(1);
                layers.put(stack.UTF8("VK_LAYER_KHRONOS_validation"));
                layers.flip();
                createInfo.ppEnabledLayerNames(layers);
            }
        }
        return createInfo;
    }
/*
    @Inject(
            method = "createInstance",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/vulkan/VkDeviceCreateInfo;ppEnabledExtensionNames(Lorg/lwjgl/PointerBuffer;)Lorg/lwjgl/vulkan/VkDeviceCreateInfo;",
                    locals = LocalCapture.CAPTURE_FAILEXCEPTION
            )
    ) private static void injectExtensions(CallbackInfo ci, VkInstanceCreateInfo createInfo) {
        // Add custom extension here
        if (Vulkan.ENABLE_VALIDATION_LAYERS){
            try (MemoryStack stack = MemoryStack.stackPush()) {

                // Example: adding standard Vulkan validation layers
                PointerBuffer layers = stack.mallocPointer(1);
                layers.put(stack.UTF8("VK_LAYER_KHRONOS_validation"));
                layers.flip();

                // Set ppEnabledLayerNames to your layers
                Vulkan.getDeviceCreateInfo().ppEnabledLayerNames(layers);
            }
        }
    }*/
}

package net.enhalo.elaia.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.awt.*;
import java.util.Set;


@Mixin(Vulkan.class)
public abstract class VulkanInstanceExtensionMixin {
    @Unique
    private static final Set<String> InstanceExtensions = Set.of("VK_EXT_debug_utils");

    @ModifyReturnValue(
            method = "getRequiredInstanceExtensions",
            at = @At("RETURN")
    )
    private static PointerBuffer getRequiredInstanceExtensions(PointerBuffer  original) {
        if (!Vulkan.ENABLE_VALIDATION_LAYERS) return original;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer buffer = stack.mallocPointer(original.remaining() + InstanceExtensions.size());

            // Copy old extensions
            for (int i = 0; i < original.remaining(); i++) {
                buffer.put(original.get(i));
            }

            // Add debug utils
            for (String extension : InstanceExtensions){
                buffer.put(stack.UTF8(extension));
            }
            buffer.flip();
            return buffer;
        }
    }
}

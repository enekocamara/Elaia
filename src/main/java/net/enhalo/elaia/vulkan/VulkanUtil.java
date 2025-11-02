package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

public abstract class VulkanUtil {
    public static int findMemoryType(int typeFilter, int properties) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.malloc(stack);
            VK10.vkGetPhysicalDeviceMemoryProperties(Vulkan.getVkDevice().getPhysicalDevice(), memProperties);

            for (int i = 0; i < memProperties.memoryTypeCount(); i++) {
                if ((typeFilter & (1 << i)) != 0
                        && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                    return i;
                }
            }

            throw new RuntimeException("Failed to find suitable memory type!");
        }
    }
}

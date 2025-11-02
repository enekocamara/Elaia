package net.enhalo.elaia.vulkan.image;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import java.nio.LongBuffer;

import static net.vulkanmod.vulkan.device.DeviceManager.device;

public class VulkanSampler {
    private final long samplerHandle;

    public VulkanSampler(){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSamplerCreateInfo samplerInfo = VkSamplerCreateInfo.calloc(stack);
            samplerInfo.sType(VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO);
            samplerInfo.magFilter(VK10.VK_FILTER_LINEAR);
            samplerInfo.minFilter(VK10.VK_FILTER_LINEAR);
            samplerInfo.addressModeU(VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.addressModeV(VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.addressModeW(VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT);
            samplerInfo.anisotropyEnable(false);
            samplerInfo.borderColor(VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK);
            samplerInfo.unnormalizedCoordinates(false);
            samplerInfo.compareEnable(false);
            samplerInfo.mipmapMode(VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR);

            LongBuffer pSampler = stack.mallocLong(1);
            if (VK10.vkCreateSampler(Vulkan.getVkDevice(), samplerInfo, null, pSampler) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create sampler!");
            }
            samplerHandle = pSampler.get(0);
        }
    }

    public long getSamplerHandle() {
        return samplerHandle;
    }
}

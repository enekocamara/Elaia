package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageCreateInfo;

public class VulkanTexture {
    private final long handle;

    VulkanTexture(VkImageCreateInfo info){
        VkDevice device = Vulkan.getVkDevice();
        long[] handle_ptr = new long[1];
        VK10.vkCreateImage(device, info, null, handle_ptr);
        this.handle = handle_ptr[0];
    }
    VkImageCreateInfo imageInfo = VkImageCreateInfo.calloc()
            .sType(VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
            .imageType(VK10.VK_IMAGE_TYPE_2D)
            .format(VK10.VK_FORMAT_R8G8B8A8_UNORM)
            .extent(e -> e.width(512).height(512).depth(1))
            .mipLevels(1)
            .arrayLayers(1)
            .samples(VK10.VK_SAMPLE_COUNT_1_BIT)
            .tiling(VK10.VK_IMAGE_TILING_OPTIMAL)
            .usage(VK10.VK_IMAGE_USAGE_SAMPLED_BIT | VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT)
            .sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE)
            .initialLayout(VK10.VK_IMAGE_LAYOUT_UNDEFINED);
}

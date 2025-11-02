package net.enhalo.elaia.vulkan.image;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import javax.swing.text.html.ImageView;
import java.nio.LongBuffer;

public class VulkanImageView {
    private final long imageViewHandle;

    public VulkanImageView(VulkanImage image){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack);
            viewInfo.sType(VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            viewInfo.image(image.getImageHandle());
            viewInfo.viewType(VK10.VK_IMAGE_VIEW_TYPE_2D);
            viewInfo.format(image.getFormat());

            VkImageSubresourceRange subresourceRange = viewInfo.subresourceRange();
            subresourceRange.aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT);
            subresourceRange.baseMipLevel(0);
            subresourceRange.levelCount(1);
            subresourceRange.baseArrayLayer(0);
            subresourceRange.layerCount(1);

            LongBuffer pView = stack.mallocLong(1);
            if (VK10.vkCreateImageView(Vulkan.getVkDevice(), viewInfo, null, pView) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create image view!");
            }
            imageViewHandle = pView.get(0);
        }
    }
    public VulkanImageView(VulkanImage2D image){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack);
            viewInfo.sType(VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            viewInfo.image(image.getImage().getImageHandle());
            viewInfo.viewType(VK10.VK_IMAGE_VIEW_TYPE_2D);
            viewInfo.format(image.getImage().getFormat());

            VkImageSubresourceRange subresourceRange = viewInfo.subresourceRange();
            subresourceRange.aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT);
            subresourceRange.baseMipLevel(0);
            subresourceRange.levelCount(1);
            subresourceRange.baseArrayLayer(0);
            subresourceRange.layerCount(1);

            LongBuffer pView = stack.mallocLong(1);
            if (VK10.vkCreateImageView(Vulkan.getVkDevice(), viewInfo, null, pView) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create image view!");
            }
            imageViewHandle = pView.get(0);
        }
    }

    public long getImageViewHandle() {
        return imageViewHandle;
    }
}

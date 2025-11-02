package net.enhalo.elaia.vulkan.image;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.util.function.Consumer;

public class VulkanTexture2D {
    private final VulkanTexture texture;
    private VulkanTexture2D(VkImageCreateInfo info){
        texture = new VulkanTexture(info);
    }

    public static class Builder{
        private final VkImageCreateInfo info = VkImageCreateInfo.calloc()
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

        public Builder extent(Consumer<VkExtent3D> consumer){
            this.info.extent(consumer);
            return this;
        }
        public Builder format(int format){
            this.info.format(format);
            return this;
        }
        public Builder usage(int usage){
            this.info.usage(usage);
            return this;
        }

        public VulkanTexture2D build(){
            return new VulkanTexture2D(info);
        }
    }
}

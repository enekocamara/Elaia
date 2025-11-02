package net.enhalo.elaia.vulkan.image;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static net.enhalo.elaia.vulkan.VulkanUtil.findMemoryType;

public class VulkanImage {
    private final long imageHandle;
    private final int format;
    private final long memoryHandle;

    VulkanImage(VkImageCreateInfo info){
        this.format = info.format();
        VkDevice device = Vulkan.getVkDevice();
        long[] handle_ptr = new long[1];
        VK10.vkCreateImage(device, info, null, handle_ptr);
        this.imageHandle = handle_ptr[0];


        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkMemoryRequirements memRequirements = VkMemoryRequirements.calloc(stack);

            VK10.vkGetImageMemoryRequirements(device, imageHandle, memRequirements);

            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    .allocationSize(memRequirements.size())
                    .memoryTypeIndex(findMemoryType(
                            memRequirements.memoryTypeBits(),
                            VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
                    ));

            LongBuffer pMemory = stack.mallocLong(1);
            VK10.vkAllocateMemory(device, allocInfo, null, pMemory);
            memoryHandle = pMemory.get(0);

            VK10.vkBindImageMemory(device, imageHandle, memoryHandle, 0);
        }
    }

    public int getFormat() {
        return format;
    }

    public long getImageHandle() {
        return imageHandle;
    }
    public long getMemoryHandle() {
        return memoryHandle;
    }
}

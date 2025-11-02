package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;

import java.nio.LongBuffer;

public class VulkanDescriptorSet {
    private final long descriptorSetHandle;

    public VulkanDescriptorSet(VulkanDescriptorSetLayout layout, long descriptorPoolHandle) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                    .descriptorPool(descriptorPoolHandle)                 // previously created pool
                    .pSetLayouts(stack.longs(layout.descriptor_set_layout_handle)); // layout handle

            LongBuffer pDescriptorSet = stack.mallocLong(1);
            VK10.vkAllocateDescriptorSets(Vulkan.getVkDevice(), allocInfo, pDescriptorSet);
            descriptorSetHandle = pDescriptorSet.get(0);
        }
    }

    public long getDescriptorSetHandle() {
        return descriptorSetHandle;
    }
}

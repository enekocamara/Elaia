package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;

import java.nio.LongBuffer;

public class VulkanPipelineLayout {
    public final long pipelineLayoutHandle;
    public VulkanPipelineLayout(VulkanDescriptorSetLayout.Buffer layouts){
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer layoutsLong = stack.mallocLong(layouts.getLayouts().size());
            for (int i = 0; i < layouts.getLayouts().size(); i++) {
                layoutsLong.put(i, layouts.getLayouts().get(i).descriptor_set_layout_handle); // fill handles
            }
            VkPushConstantRange.Buffer emptyRanges = VkPushConstantRange.calloc(0, stack);
            VkPipelineLayoutCreateInfo layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                    .pNext(0)
                    .flags(0)
                    .pSetLayouts(layoutsLong) //VkDescriptorSetLayout
                    .pPushConstantRanges(emptyRanges);

            LongBuffer pPipelineLayout = stack.mallocLong(1);
            if (VK10.vkCreatePipelineLayout(Vulkan.getVkDevice(), layoutInfo, null, pPipelineLayout) != VK10.VK_SUCCESS) {
                throw new RuntimeException("VulkanComputePipeline: Failed to create pipeline layout");
            }
            pipelineLayoutHandle = pPipelineLayout.get(0);
        }
    }
}

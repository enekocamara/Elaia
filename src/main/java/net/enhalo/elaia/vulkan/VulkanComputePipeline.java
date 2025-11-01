package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

public class VulkanComputePipeline {
    private final VulkanPipelineLayout layout;
    private final VulkanShaderModule shaderModule;
    private final long pipelineHandle;
    public VulkanComputePipeline(VulkanPipelineLayout layout, VulkanShaderModule shaderModule){
        this.layout = layout;
        this.shaderModule = shaderModule;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkComputePipelineCreateInfo.Buffer pipelineInfo = VkComputePipelineCreateInfo.calloc(1, stack);
            pipelineInfo.get(0)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO)
                    .layout(layout.pipelineLayoutHandle)
                    .stage(VkPipelineShaderStageCreateInfo.calloc(stack)
                            .sType(VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                            .stage(VK10.VK_SHADER_STAGE_COMPUTE_BIT)
                            .module(shaderModule.getShaderModuleHandle())
                            .pName(stack.ASCII("main"))); // entry point

            LongBuffer pPipeline = stack.mallocLong(1);
            int err = VK10.vkCreateComputePipelines(
                    Vulkan.getVkDevice(), // your VkDevice
                    VK10.VK_NULL_HANDLE,  // optional pipeline cache
                    pipelineInfo,
                    null,
                    pPipeline
            );

            if (err != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create compute pipeline: ");
            }

            this.pipelineHandle = pPipeline.get(0);
        }
    }

    public long getPipelineHandle() {
        return pipelineHandle;
    }

    public VulkanPipelineLayout getLayout() {
        return layout;
    }

    public VulkanShaderModule getShaderModule() {
        return shaderModule;
    }
}
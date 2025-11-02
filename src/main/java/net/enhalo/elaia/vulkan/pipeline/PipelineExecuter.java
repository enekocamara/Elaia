package net.enhalo.elaia.vulkan;

import net.enhalo.elaia.Elaia;
import net.vulkanmod.vulkan.Vulkan;
import net.vulkanmod.vulkan.device.DeviceManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

public class PipelineExecuter {
    public static void executePipeline(VulkanComputePipeline pipeline, VulkanDescriptorSet descriptorSet) {
        asyncExecutePipeline(pipeline, descriptorSet);
    }
    public static void asyncExecutePipeline(VulkanComputePipeline pipeline, VulkanDescriptorSet descriptorSet) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            long commandPool = DeviceManager.getComputeQueue().getCommandPool().getId();
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool) // A pool created with VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            VK10.vkAllocateCommandBuffers(Vulkan.getVkDevice(), allocInfo, pCommandBuffer);

            VkCommandBuffer commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), Vulkan.getVkDevice());

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                    .flags(VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

            VK10.vkBeginCommandBuffer(commandBuffer, beginInfo);
            VK10.vkCmdBindPipeline(commandBuffer, VK10.VK_PIPELINE_BIND_POINT_COMPUTE, pipeline.getPipelineHandle());
            LongBuffer descriptorSets = stack.longs(descriptorSet.getDescriptorSetHandle());



            VK10.vkCmdBindDescriptorSets(
                    commandBuffer,
                    VK10.VK_PIPELINE_BIND_POINT_COMPUTE,
                    pipeline.getLayout().pipelineLayoutHandle,
                    0, descriptorSets, null
            );

            // Dispatch compute shader
            VK10.vkCmdDispatch(commandBuffer, 1, 1, 1);

            VK10.vkEndCommandBuffer(commandBuffer);

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(stack.pointers(commandBuffer.address()));

            VK10.vkQueueSubmit(DeviceManager.getComputeQueue().queue(), submitInfo, VK10.VK_NULL_HANDLE);
            VK10.vkQueueWaitIdle(DeviceManager.getComputeQueue().queue()); // Wait for GPU to finish
            Elaia.LOGGER.info("Finished compute shader pass");
        }
    }
}

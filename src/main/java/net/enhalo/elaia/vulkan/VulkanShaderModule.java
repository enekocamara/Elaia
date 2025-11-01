package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.vulkan.VK10.*;

/*
Create buffers/images in GPU memory (VkBuffer or VkImage), potentially using VulkanMod helpers.

Compile your compute shader to SPIR-V.

Create a shader module (VkShaderModule) from SPIR-V.

Create a pipeline layout describing descriptor sets.

Create a compute pipeline with the shader module and layout.

Allocate descriptor sets and update them with your buffers/images.

Record a command buffer:

Bind pipeline

Bind descriptor sets

Dispatch workgroups

Submit command buffer to GPU queue.

Wait for completion or continue asynchronously.
 */
public class VulkanShaderModule {
    private final long shaderModuleHandle;

    public VulkanShaderModule(String spvPath) throws IOException, RuntimeException{

        ByteBuffer shaderBytes = ShaderCompiler.compileToByte(spvPath);
        ByteBuffer shaderBuffer = ByteBuffer.allocateDirect(shaderBytes.capacity());
        shaderBuffer.put(shaderBytes).flip();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo info = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pNext(0)
                    .flags(0)
                    .pCode(shaderBuffer);

            LongBuffer pShaderModule = stack.mallocLong(1);

            if (vkCreateShaderModule(Vulkan.getVkDevice(), info, null, pShaderModule) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create shader module");
            }
            shaderModuleHandle = pShaderModule.get(0);
        }
    }
    public long getShaderModuleHandle() {
        return shaderModuleHandle;
    }
}

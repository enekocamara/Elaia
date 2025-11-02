package net.enhalo.elaia.vulkan.descriptor;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

import java.nio.LongBuffer;
import java.util.List;

public class VulkanDescriptorSetLayout {
    public final long descriptor_set_layout_handle;

    private VulkanDescriptorSetLayout(VkDescriptorSetLayoutBinding.Buffer buffer) throws RuntimeException{
        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                    .pBindings(buffer);
            LongBuffer pLayout = stack.mallocLong(1);
            if (VK10.vkCreateDescriptorSetLayout(Vulkan.getVkDevice(), info, null, pLayout) != VK10.VK_SUCCESS)
                throw new RuntimeException("VulkanDescriptorSetLayout: failed to create descriptor set layout");
            descriptor_set_layout_handle = pLayout.get(0);
        }
    }

    public static class Builder{
        private final VkDescriptorSetLayoutBinding.Buffer buffer;
        private int asignedBindingCount = 0;
        public Builder(int count){
            //try (MemoryStack stack = MemoryStack.stackPush()){
                buffer = VkDescriptorSetLayoutBinding.calloc(count);
            //}
        }
        public Builder setBinding(int type, int count, int stageFlags) throws RuntimeException{
            if (asignedBindingCount > buffer.capacity())
                throw new RuntimeException("VulnakDescriptorSetLayout:Builder:setBinding: tried setting more biddings than requested");
            buffer.get(asignedBindingCount)
                .binding(asignedBindingCount)
                    .descriptorType(type)
                    .descriptorCount(count)
                    .stageFlags(stageFlags)
                    .pImmutableSamplers(null);
            asignedBindingCount += 1;
            return this;
        }

        public VulkanDescriptorSetLayout build() throws RuntimeException{
            if (asignedBindingCount != buffer.capacity()){
                throw new RuntimeException("VulnakDescriptorSetLayout:Builder:build: some bindings were not asigned");
            }
            return new VulkanDescriptorSetLayout(buffer);
        }

    }

    public static class Buffer{
        private final List<VulkanDescriptorSetLayout> layouts;

        public Buffer(List<VulkanDescriptorSetLayout> layouts) {
            this.layouts = layouts;
        }

        public List<VulkanDescriptorSetLayout> getLayouts() {
            return layouts;
        }
    }
}

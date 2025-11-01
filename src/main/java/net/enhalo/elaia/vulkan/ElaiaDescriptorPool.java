package net.enhalo.elaia.vulkan;

import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;

import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.Map;

public class ElaiaDescriptorPool {
    private final  Map<Integer, Integer> poolSizeMap;
    private VkDescriptorPoolSize.Buffer poolSizes;
    private long descriptorPoolHandle;
    private boolean initialized = false;
    public ElaiaDescriptorPool(){
        poolSizeMap = new HashMap<>();
    }

    void addPoolSize(int type, int count) throws RuntimeException{
        if (initialized){
            throw new RuntimeException("ElaiaDescriptorPool:addPoolSize: DescriptorPool already initialized");
        }
        if (poolSizeMap.containsKey(type)){
            if (poolSizeMap.get(type) < count)
                poolSizeMap.put(type, count);
        }else
            poolSizeMap.put(type, count);
    }


    public void initialize(int sets){
        initialized = true;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Define how many of each descriptor type the pool can hold

            VkDescriptorPoolSize.Buffer poolSizes = VkDescriptorPoolSize.calloc(poolSizeMap.size(), stack);
            int i = 0;
            for (Map.Entry<Integer, Integer> value : poolSizeMap.entrySet()){
                poolSizes.get(i)
                        .type(value.getKey())
                        .descriptorCount(value.getValue());
                i++;
            }
            // Create the descriptor pool info
            VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                    .pNext(0)
                    .flags(0)
                    .maxSets(sets)           // maximum descriptor sets you can allocate from this pool
                    .pPoolSizes(poolSizes);

            LongBuffer pDescriptorPool = stack.mallocLong(1);
            if (VK10.vkCreateDescriptorPool(Vulkan.getVkDevice(), poolInfo, null, pDescriptorPool) != VK10.VK_SUCCESS) {
                throw new RuntimeException("Failed to create descriptor pool");
            }

            descriptorPoolHandle = pDescriptorPool.get(0);
        }
    }

    public long getDescriptorPoolHandle() throws RuntimeException{
        if (!initialized){
            throw new RuntimeException("ElaiaDescriptorPool:getDescriptorPoolHandle: tried to get handle before initialization");
        }
        return descriptorPoolHandle;
    }
}

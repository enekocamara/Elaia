package net.enhalo.elaia.mixin;

import net.enhalo.elaia.Elaia;
import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.IntBuffer;
import java.util.Set;

import static org.lwjgl.vulkan.EXTValidationFeatures.VK_VALIDATION_FEATURE_ENABLE_BEST_PRACTICES_EXT;
import static org.lwjgl.vulkan.EXTValidationFeatures.VK_VALIDATION_FEATURE_ENABLE_SYNCHRONIZATION_VALIDATION_EXT;
import static org.lwjgl.vulkan.EXTValidationFeatures.VK_VALIDATION_FEATURE_ENABLE_DEBUG_PRINTF_EXT;
import static org.lwjgl.vulkan.EXTValidationFeatures.VK_VALIDATION_FEATURE_ENABLE_GPU_ASSISTED_RESERVE_BINDING_SLOT_EXT;
import static org.lwjgl.vulkan.EXTValidationFeatures.VK_VALIDATION_FEATURE_ENABLE_GPU_ASSISTED_EXT;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

@Mixin(Vulkan.class)
public abstract class ValidationLayerMixin {

    @Shadow @Final
    public static boolean ENABLE_VALIDATION_LAYERS = VulkanConfig.ENABLE_VALIDATION_LAYERS;

    @ModifyVariable(
            method = "createInstance",
            at = @At(
                value = "STORE",
                ordinal = 0
            )
    )
    private static VkInstanceCreateInfo modifyCreateInfo(VkInstanceCreateInfo  createInfo) {
        if (VulkanConfig.ENABLE_VALIDATION_LAYERS){


            int[] count = new int[1];
            VK10.vkEnumerateInstanceLayerProperties(count, null); // get number of layers

            VkLayerProperties.Buffer layers2 = VkLayerProperties.calloc(count[0]);
            VK10.vkEnumerateInstanceLayerProperties(count, layers2);

            for (int i = 0; i < layers2.capacity(); i++) {
                VkLayerProperties layer = layers2.get(i);
                String name = layer.layerNameString(); // actually exists in LWJGL
                Elaia.LOGGER.info("Available layer: " + name);
            }


            //try (MemoryStack stack = MemoryStack.stackPush()) {
                // Add validation layer
                PointerBuffer layers = MemoryUtil.memAllocPointer(1);
                layers.put(MemoryUtil.memUTF8("VK_LAYER_KHRONOS_validation"));
                layers.flip();
                createInfo.ppEnabledLayerNames(layers);


                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.create();
                debugCreateInfo.sType(EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
                debugCreateInfo.messageSeverity(
                        EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT
                );
                debugCreateInfo.messageType(
                        EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT |
                                EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT
                );

                int[] allFeatures = new int[] {
                    VK_VALIDATION_FEATURE_ENABLE_GPU_ASSISTED_EXT,
                    VK_VALIDATION_FEATURE_ENABLE_GPU_ASSISTED_RESERVE_BINDING_SLOT_EXT,
                    VK_VALIDATION_FEATURE_ENABLE_BEST_PRACTICES_EXT,
                    VK_VALIDATION_FEATURE_ENABLE_DEBUG_PRINTF_EXT,
                    VK_VALIDATION_FEATURE_ENABLE_SYNCHRONIZATION_VALIDATION_EXT
                };

                IntBuffer featuresBuffer = MemoryUtil.memAllocInt(allFeatures.length);
                featuresBuffer.put(allFeatures).flip();

                VkValidationFeaturesEXT validationFeatures = VkValidationFeaturesEXT.create();
                validationFeatures.sType(EXTValidationFeatures.VK_STRUCTURE_TYPE_VALIDATION_FEATURES_EXT);
                //validationFeatures.enabledValidationFeatureCount(1);
                validationFeatures.pEnabledValidationFeatures(featuresBuffer);
                debugCreateInfo.pNext(validationFeatures.address());

                debugCreateInfo.pfnUserCallback(new VkDebugUtilsMessengerCallbackEXTI() {
                    @Override
                    public int invoke(int messageSeverity, int messageTypes, long pCallbackData, long pUserData) {
                        VkDebugUtilsMessengerCallbackDataEXT data = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                        Elaia.LOGGER.info("[VULKAN] {}", data.pMessageString());
                        return VK10.VK_FALSE;
                    }
                });
                createInfo.pNext(debugCreateInfo.address());

                Elaia.LOGGER.info("Validation layer set");
            }
        //}
        return createInfo;
    }
    /*
    @Inject(
            method = "createInstance",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/vulkan/VkDeviceCreateInfo;ppEnabledExtensionNames(Lorg/lwjgl/PointerBuffer;)Lorg/lwjgl/vulkan/VkDeviceCreateInfo;",
                    locals = LocalCapture.CAPTURE_FAILEXCEPTION
            )
    ) private static void injectExtensions(CallbackInfo ci, VkInstanceCreateInfo createInfo) {
        // Add custom extension here
        if (Vulkan.ENABLE_VALIDATION_LAYERS){
            try (MemoryStack stack = MemoryStack.stackPush()) {

                // Example: adding standard Vulkan validation layers
                PointerBuffer layers = stack.mallocPointer(1);
                layers.put(stack.UTF8("VK_LAYER_KHRONOS_validation"));
                layers.flip();

                // Set ppEnabledLayerNames to your layers
                Vulkan.getDeviceCreateInfo().ppEnabledLayerNames(layers);
            }
        }
    }*/
}

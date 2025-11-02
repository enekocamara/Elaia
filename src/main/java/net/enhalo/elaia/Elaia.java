package net.enhalo.elaia;

import net.enhalo.elaia.vulkan.ElaiaDescriptorPool;
import net.enhalo.elaia.vulkan.VulkanCommandPool;
import net.enhalo.elaia.vulkan.VulkanInitializer;
import net.enhalo.elaia.worldgen.ElaiaChunkGenerator;
import net.enhalo.elaia.worldgen.WorldManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.vulkanmod.vulkan.Vulkan;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class Elaia implements ModInitializer {
	public static final String MOD_ID = "elaia";
    public static final WorldManager worldManager = new WorldManager();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ElaiaDescriptorPool DESCRIPTOR_POOL = new ElaiaDescriptorPool();
    public static final VulkanCommandPool computeCommandPool = new VulkanCommandPool();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

        Registry.register(Registries.CHUNK_GENERATOR, Identifier.of(Elaia.MOD_ID, "chunkgenerator"),
                ElaiaChunkGenerator.CODEC);

        VulkanInitializer.onInitialized(() -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.calloc(stack);
                VK10.vkGetPhysicalDeviceProperties(Vulkan.getVkDevice().getPhysicalDevice(), properties);

                // Query enabled extensions
                IntBuffer pExtensionCount = stack.ints(0);
                VK10.vkEnumerateDeviceExtensionProperties(Vulkan.getVkDevice().getPhysicalDevice(), (ByteBuffer) null, pExtensionCount, null);

                VkExtensionProperties.Buffer extensions = VkExtensionProperties.calloc(pExtensionCount.get(0), stack);
                VK10.vkEnumerateDeviceExtensionProperties(Vulkan.getVkDevice().getPhysicalDevice(), (ByteBuffer) null, pExtensionCount, extensions);

                Set<String> requested_extensions = new HashSet<>(Set.of(
                        "VK_KHR_shader_float16_int8",
                        "VK_KHR_16bit_storage"
                ));

                for (int i = 0; i < extensions.capacity(); i++) {
                    String name = extensions.get(i).extensionNameString();
                    requested_extensions.remove(name);

                }
                if (!requested_extensions.isEmpty()) {
                    LOGGER.error("Extensions not set: " + extensions);
                    throw new RuntimeException("Extensions not set: " + extensions);
                }
                LOGGER.info("ALL EXTENSION SUPPORTED");
            }

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer count = stack.ints(0);
                VK10.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, null); // total count
                VkExtensionProperties.Buffer props = VkExtensionProperties.calloc(count.get(0), stack);
                VK10.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, props);

                LOGGER.info("CHEKING FOR EXT layer");
                for (int i = 0; i < props.capacity(); i++) {
                    String extName = props.get(i).extensionNameString();
                    if (extName.equals("VK_EXT_debug_utils")) {
                        LOGGER.info("Debug utils extension is available!");
                    }
                }
                LOGGER.info("LAYER CHECK FINISHED");

                //initialize pool
                DESCRIPTOR_POOL.addPoolSize(VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, 1);
                DESCRIPTOR_POOL.initialize(1);

            }

        });

	}
}
package net.enhalo.elaia.vulkan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VulkanInitializer {
    private static final List<VulkanCallback> listeners = new ArrayList<>();
    private static volatile boolean initialized = false;

    @FunctionalInterface
    public interface VulkanCallback {
        void run();
    }

    // Call this once VulkanMod finishes initVulkan
    public static synchronized void signalInitialized() {
        initialized = true;
        for (VulkanCallback r : listeners) {
            r.run();
        }
        listeners.clear(); // optional, we don’t need them anymore
    }

    // Attach a function to run after Vulkan is initialized
    public static synchronized void onInitialized(VulkanCallback listener) {
        if (initialized) {
            listener.run(); // run immediately if already initialized
        } else {
            listeners.add(listener);
        }
    }

    // Optionally, wait until Vulkan is initialized (blocking)
    public static void waitForInitialization() {
        while (!initialized) {
            try {
                Thread.sleep(1); // small sleep to avoid busy-wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
}

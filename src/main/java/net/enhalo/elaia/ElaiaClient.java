package net.enhalo.elaia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ElaiaClient implements ClientModInitializer {
    public static KeyBinding openShaderScreen;
    @Override
    public void onInitializeClient() {
        openShaderScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tutorialmod.openshader", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P, // example key
                "category.elaia.main"
        ));
    }
}
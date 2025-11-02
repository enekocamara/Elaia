package net.enhalo.elaia.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.enhalo.elaia.Elaia;
import net.enhalo.elaia.vulkan.image.LinkedImage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.vulkanmod.gl.VkGlProgram;
import net.vulkanmod.gl.VkGlTexture;
import net.vulkanmod.mixin.render.RenderSystemMixin;
import net.vulkanmod.vulkan.VRenderSystem;
import net.vulkanmod.vulkan.texture.VulkanImage;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NoisePreviewScreen extends Screen {

    //private final Identifier textId;
    //private final int openpltextId;
    //private final LinkedImage image;
    //private final VkGlTexture texture;
    private final VkGlTexture texture;
    private final VkGlProgram program;
    private final LinkedImage linkedImage;

    public NoisePreviewScreen(int width, int height, VulkanImage image, String name) {
        super(Text.literal("World Preview"));
        RenderSystem.assertOnRenderThreadOrInit();
        //this.texture = new VkGlTexture(, "continental_image");
        int id = VkGlTexture.genTextureId();
        texture = new VkGlTexture(id);
        texture.setVulkanImage(image);

        int id2 = VkGlProgram.genProgramId();
        program = VkGlProgram.getProgram(id2);
        //program.bindPipeline();
        linkedImage = new LinkedImage(texture, "world_map");

        //this.textId = OpenglLinkedTexture.wrapExistingGLTexture(textureID, name);
        //this.openpltextId = textureID;

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        Elaia.LOGGER.info("render runs");

        //int texId = tex; // your texture handle (e.g. generated from GL11.glGenTextures)
        //TutorialMod.LOGGER.info("texture id" + this.openpltextId);
        //RenderSystem.setShaderTexture(0, this.texture_id);
        //RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        //RenderSystem.setShader(GameRenderer::getP);
        //RenderSystemMixin

        // set white color (no tint)
        //RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // x, y, width, height — screen position and size
        context.drawTexture(
                RenderLayer::getGuiTextured, linkedImage.getId(),  // OpenGL texture ID
                10, 10, // x, y position on screen
                0, 0,   // u, v start
                256, 256, // draw width/height
                256, 256  // texture width/height
        );

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }


}

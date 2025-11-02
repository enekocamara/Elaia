package net.enhalo.elaia.vulkan.image;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.enhalo.elaia.Elaia;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.vulkanmod.gl.VkGlTexture;

public class LinkedImage  extends AbstractTexture {

    private final VkGlTexture texture;
    private final Identifier id;

    public LinkedImage(VkGlTexture texture, String name) {
        this.texture = texture;
        id = Identifier.of(Elaia.MOD_ID, name);
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, this);
    }

    @Override
    public void bindTexture() {
        VkGlTexture.bindTexture(texture.id);
    }


    public VkGlTexture getTexture() {
        return texture;
    }

    public Identifier getId(){
        return id;
    }
}

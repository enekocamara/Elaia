package net.enhalo.elaia.mixin;



import com.mojang.blaze3d.systems.RenderSystem;
import net.enhalo.elaia.Elaia;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    @Nullable
    private ClientWorld world;
    @Shadow @Final
    private MinecraftClient client;
    @Shadow private int cameraChunkX;
    @Shadow private int cameraChunkY;
    @Shadow private int cameraChunkZ;
    @Shadow private double lastCameraX;
    @Shadow private double lastCameraY;
    @Shadow private double lastCameraZ;
    @Shadow private int viewDistance;
    @Shadow private double lastCameraPitch;
    @Shadow private double lastCameraYaw;

    @Shadow @Final
    private DefaultFramebufferSet framebufferSet;
    @Shadow @Final
    private ChunkRenderingDataPreparer chunkRenderingDataPreparer;
    @Shadow
    private void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {

    }

    @Shadow
    public static Frustum offsetFrustum(Frustum frustum) {
        throw new RuntimeException("a");
    }

    @Shadow
    private void applyFrustum(Frustum frustum) {

    }



        @Shadow
    private void updateChunks(Camera camera) {

    }
    @Shadow @Nullable
    private BuiltChunkStorage chunks;

    /*@Inject(
            method = "setupTerrain",
            at = @At("HEAD"),
            cancellable = true
    )*/private void setupTerrainMine(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator) {
        WorldRenderer renderer = (WorldRenderer) (Object) this;
        Vec3d vec3d = camera.getPos();
        if (this.client.options.getClampedViewDistance() != this.viewDistance) {
            renderer.reload();
        }

        Profiler profiler = Profilers.get();
        profiler.push("camera");
        int i = ChunkSectionPos.getSectionCoord(vec3d.getX());
        int j = ChunkSectionPos.getSectionCoord(vec3d.getY());
        int k = ChunkSectionPos.getSectionCoord(vec3d.getZ());
        if (this.cameraChunkX != i || this.cameraChunkY != j || this.cameraChunkZ != k) {
            this.cameraChunkX = i;
            this.cameraChunkY = j;
            this.cameraChunkZ = k;
            this.chunks.updateCameraPosition(ChunkSectionPos.from(vec3d));
        }

        renderer.getChunkBuilder().setCameraPosition(vec3d);
        profiler.swap("cull");
        double d = Math.floor(vec3d.x / (double)8.0F);
        double e = Math.floor(vec3d.y / (double)8.0F);
        double f = Math.floor(vec3d.z / (double)8.0F);
        if (d != this.lastCameraX || e != this.lastCameraY || f != this.lastCameraZ) {
            this.chunkRenderingDataPreparer.scheduleTerrainUpdate();
        }

        this.lastCameraX = d;
        this.lastCameraY = e;
        this.lastCameraZ = f;
        profiler.swap("update");
        if (!hasForcedFrustum) {
            boolean bl = this.client.chunkCullingEnabled;
            if (spectator && this.world.getBlockState(camera.getBlockPos()).isOpaqueFullCube()) {
                bl = false;
            }

            profiler.push("section_occlusion_graph");
            this.chunkRenderingDataPreparer.updateSectionOcclusionGraph(bl, camera, frustum, renderer.getBuiltChunks(), this.world.getChunkManager().getActiveSections());
            profiler.pop();
            double g = Math.floor((double)(camera.getPitch() / 2.0F));
            double h = Math.floor((double)(camera.getYaw() / 2.0F));
            if (this.chunkRenderingDataPreparer.method_52836() || g != this.lastCameraPitch || h != this.lastCameraYaw) {
                this.applyFrustum(offsetFrustum(frustum));
                this.lastCameraPitch = g;
                this.lastCameraYaw = h;
            }
        }

        profiler.pop();

    }



        @Shadow
    private Frustum frustum;
    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        Elaia.LOGGER.info("rendering");
        WorldRenderer renderer = (WorldRenderer) (Object) this;


        float f = tickCounter.getTickDelta(false);
        RenderSystem.setShaderGameTime(this.world.getTime(), f);
        this.world.runQueuedChunkUpdates();
        this.world.getChunkManager().getLightingProvider().doLightUpdates();


        boolean bl = renderer.getCapturedFrustum() != null;

        //setupTerrainMine(camera, frustum,  bl, this.client.player.isSpectator());
        setupTerrain(camera, frustum,  bl, this.client.player.isSpectator());
        //updateChunks(camera);

        this.client.getFramebuffer().beginWrite(false);
        RenderSystem.clearColor(0f, 0f, 0f, 1f);
        RenderSystem.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        this.framebufferSet.clear();
        RenderSystem.disableBlend();
        RenderSystem.setShaderFog(Fog.DUMMY);
        ci.cancel();
        /*
        this.blockEntityRenderDispatcher.configure(this.world, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        final Profiler profiler = Profilers.get();
        profiler.swap("light_update_queue");
        this.world.runQueuedChunkUpdates();
        profiler.swap("light_updates");
        this.world.getChunkManager().getLightingProvider().doLightUpdates();
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double g = vec3d.getZ();
        profiler.swap("culling");
        boolean bl = this.capturedFrustum != null;
        Frustum frustum = bl ? this.capturedFrustum : this.frustum;
        Profilers.get().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.capturedFrustum = bl ? new Frustum(positionMatrix, projectionMatrix) : frustum;
            this.capturedFrustum.setPosition(d, e, g);
            this.shouldCaptureFrustum = false;
        }

        profiler.swap("fog");
        float h = gameRenderer.getViewDistance();
        boolean bl2 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        Vector4f vector4f = BackgroundRenderer.getFogColor(camera, f, this.client.world, this.client.options.getClampedViewDistance(), gameRenderer.getSkyDarkness(f));
        Fog fog = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, vector4f, h, bl2, f);
        Fog fog2 = BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, vector4f, h, bl2, f);
        profiler.swap("cullEntities");
        boolean bl3 = this.getEntitiesToRender(camera, frustum, this.renderedEntities);
        this.renderedEntitiesCount = this.renderedEntities.size();
        profiler.swap("terrain_setup");
        this.setupTerrain(camera, frustum, bl, this.client.player.isSpectator());
        profiler.swap("compile_sections");
        this.updateChunks(camera);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul(positionMatrix);
        FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
        this.framebufferSet.mainFramebuffer = frameGraphBuilder.createObjectNode("main", this.client.getFramebuffer());
        int i = this.client.getFramebuffer().textureWidth;
        int j = this.client.getFramebuffer().textureHeight;
        SimpleFramebufferFactory simpleFramebufferFactory = new SimpleFramebufferFactory(i, j, true);
        PostEffectProcessor postEffectProcessor = this.getTransparencyPostEffectProcessor();
        if (postEffectProcessor != null) {
            this.framebufferSet.translucentFramebuffer = frameGraphBuilder.createResourceHandle("translucent", simpleFramebufferFactory);
            this.framebufferSet.itemEntityFramebuffer = frameGraphBuilder.createResourceHandle("item_entity", simpleFramebufferFactory);
            this.framebufferSet.particlesFramebuffer = frameGraphBuilder.createResourceHandle("particles", simpleFramebufferFactory);
            this.framebufferSet.weatherFramebuffer = frameGraphBuilder.createResourceHandle("weather", simpleFramebufferFactory);
            this.framebufferSet.cloudsFramebuffer = frameGraphBuilder.createResourceHandle("clouds", simpleFramebufferFactory);
        }

        if (this.entityOutlineFramebuffer != null) {
            this.framebufferSet.entityOutlineFramebuffer = frameGraphBuilder.createObjectNode("entity_outline", this.entityOutlineFramebuffer);
        }

        RenderPass renderPass = frameGraphBuilder.createPass("clear");
        this.framebufferSet.mainFramebuffer = renderPass.transfer(this.framebufferSet.mainFramebuffer);
        renderPass.setRenderer(() -> {
            RenderSystem.clearColor(vector4f.x, vector4f.y, vector4f.z, 0.0F);
            RenderSystem.clear(16640);
        });
        if (!bl2) {
            this.renderSky(frameGraphBuilder, camera, f, fog2);
        }

        this.renderMain(frameGraphBuilder, frustum, camera, positionMatrix, projectionMatrix, fog, renderBlockOutline, bl3, tickCounter, profiler);
        PostEffectProcessor postEffectProcessor2 = this.client.getShaderLoader().loadPostEffect(ENTITY_OUTLINE, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
        if (bl3 && postEffectProcessor2 != null) {
            postEffectProcessor2.render(frameGraphBuilder, i, j, this.framebufferSet);
        }

        this.renderParticles(frameGraphBuilder, camera, f, fog);
        CloudRenderMode cloudRenderMode = this.client.options.getCloudRenderModeValue();
        if (cloudRenderMode != CloudRenderMode.OFF) {
            float k = this.world.getDimensionEffects().getCloudsHeight();
            if (!Float.isNaN(k)) {
                float l = (float)this.ticks + f;
                int m = this.world.getCloudsColor(f);
                this.renderClouds(frameGraphBuilder, positionMatrix, projectionMatrix, cloudRenderMode, camera.getPos(), l, m, k + 0.33F);
            }
        }

        this.renderWeather(frameGraphBuilder, camera.getPos(), f, fog);
        if (postEffectProcessor != null) {
            postEffectProcessor.render(frameGraphBuilder, i, j, this.framebufferSet);
        }

        this.renderLateDebug(frameGraphBuilder, vec3d, fog);
        profiler.swap("framegraph");
        frameGraphBuilder.run(allocator, new FrameGraphBuilder.Profiler() {
            public void push(String location) {
                profiler.push(location);
            }

            public void pop(String location) {
                profiler.pop();
            }
        });
        this.client.getFramebuffer().beginWrite(false);
        this.renderedEntities.clear();
        this.framebufferSet.clear();
        matrix4fStack.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderFog(Fog.DUMMY);*/

    }
}
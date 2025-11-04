package net.enhalo.elaia.mixin;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.block.Block;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightSourceView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Chunk.class)

public abstract class ChunkMixin implements BiomeAccess.Storage, LightSourceView, StructureHolder, AttachmentTarget {
    //@Unique
    //private MyBlockStorage elaia$storage; // your custom memory layout
    //Block
}


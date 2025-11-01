package net.enhalo.elaia.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import java.util.stream.Stream;

public class EmptyBiomeSource extends BiomeSource{
    private final RegistryEntry<Biome> dummyBiome;

    public EmptyBiomeSource(RegistryEntry<Biome> biome) {
        super(); // seed array, can be anything
        this.dummyBiome = biome;
    }

    /*@Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z) {
        return dummyBiome;
    }*/

    @Override
    protected MapCodec<? extends BiomeSource> getCodec() {
        return null;
    }

    @Override
    protected Stream<RegistryEntry<Biome>> biomeStream() {
        return Stream.empty();
    }

    @Override
    public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
        return null;
    }
}

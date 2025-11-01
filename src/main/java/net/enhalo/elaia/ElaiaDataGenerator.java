package net.enhalo.elaia;

import net.enhalo.elaia.datagen.DataWorldGenerator;
import net.enhalo.elaia.worldgen.ContinentalDimensionType;
import net.enhalo.elaia.worldgen.EmptyBiome;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ElaiaDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        Elaia.LOGGER.info("Data generator init");
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DataWorldGenerator::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder){
        Elaia.LOGGER.info("building registry");
        registryBuilder.addRegistry(RegistryKeys.DIMENSION_TYPE, ContinentalDimensionType::bootstrapType);
        registryBuilder.addRegistry(RegistryKeys.BIOME, EmptyBiome::bootstrapType);
    }
}

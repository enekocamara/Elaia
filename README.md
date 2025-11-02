Minecraft Mod that aims to push world gen to the limit. It uses the VulcanMod to change minecraft renderer to Vulkan.
To be implemented:
* GPU driven world generation. When creating the world, it will simulate in the gpu tectonic plates, different erosion types and climate to create a realistic world map.
* Second branch eutheria: VERY LONG TERM PROJECT <br>
I will replace minecraft world renderer with my custom one, keeping ui and some elements intact. This is because one of the mod features is the subdivision of blocks into
(8*8)smaller blocks (maybe 16*16), allowing to have way more shapes. Each subblock will have its own stated to make the block more detailed, or be its own block type. Example instead
of a coal-stone block i will have a block made up mostly of stone with some sublocks made of coal. I will also like to implement a custom lod system to render farther chunks similar to
DistantHorizon.
The last reason for this subdivision is that each sublock will be either clean cut or smooth. So instead of only having small blocks smooth blocks will form diagonal lines to make a smother
model. This will allow for both smooth curves on blocks as well as the tipical minecraft cube look.

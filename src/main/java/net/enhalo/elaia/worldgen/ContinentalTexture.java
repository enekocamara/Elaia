package net.enhalo.elaia.worldgen;



import static net.enhalo.elaia.Elaia.LOGGER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.vulkan.VK10.*;

import net.enhalo.elaia.Elaia;
import net.enhalo.elaia.vulkan.*;
import net.minecraft.util.Pair;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.vulkan.VK10;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;

public class ContinentalTexture {
    private final VulkanTexture2D plate_text;
    private final VulkanComputePipeline plate_pipeline;
    //private final VulkanTexture2D plate_features_text;
    //private final VulkanComputePipeline plate_features_pipeline;
    //private final Map<Vec2i, Plate> plates;
    private final static int WIDTH = 2000;
    private final static int HEIGHT = 2000;

    ContinentalTexture(long seed) {
        plate_text = new VulkanTexture2D.Builder().
                extent((extent) -> {
                    extent.width(WIDTH);
                    extent.height(HEIGHT);})
                .format(VK10.VK_FORMAT_R32G32_SINT)
                .usage(VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT)
                .build();

        VulkanDescriptorSetLayout descriptorSetLayout =  new VulkanDescriptorSetLayout
                .Builder(1)
                .setBinding(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,1,VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT)
                .build();

        VulkanDescriptorSetLayout.Buffer descriptorSetLayoutBuffer =
                new VulkanDescriptorSetLayout.Buffer(List.of(descriptorSetLayout));

        try{
        plate_pipeline = new VulkanComputePipeline(
                new VulkanPipelineLayout(descriptorSetLayoutBuffer),
                new VulkanShaderModule("/assets/elaia/shaders/simple")
        );
        }catch (IOException | RuntimeException e){
            throw new RuntimeException("Failed to create vulkan compute pipeline" + e.getMessage());
        }

        VulkanDescriptorSet descriptorSet = new VulkanDescriptorSet(descriptorSetLayout, Elaia.DESCRIPTOR_POOL.getDescriptorPoolHandle());

        PipelineExecuter.executePipeline(plate_pipeline, descriptorSet);
        /*
        programRunner.runProgram(plate_pipeline.program,plate_id_text.getTextureID(), WIDTH,HEIGHT,(prog) -> {
            int loc = GL20.glGetUniformLocation(prog, "iSeed");
            GL20.glUniform1f(loc, 31.0f);

            int loc2 = GL20.glGetUniformLocation(prog, "iTextSize");
            GL20.glUniform2f(loc2, WIDTH, HEIGHT);
        });
        plates = new HashMap<>();
        fill_plates();

        // Pair<LookUp,Neighbours >
        Pair<int[], int[]> GPU_plate_data = generate_gpu_plate_data();
        //int[] GPU_plate_neighbours = generate_gpu_neighbours();
        plate_features_text = new OpenGlTexture(WIDTH, HEIGHT, GL_RGBA32F, GL_R, GL_FLOAT);
        plate_features_pipeline = new OpenglShaderProgram("/assets/tutorialmod/shaders/continental_step_2", Optional.of((src) -> {
            return src
                .replace("LOOKUPSIZE", GPU_plate_data.getLeft().toString())
                .replace("NEIGHBOURSIZE", GPU_plate_data.getRight().toString());
        }),
                Optional.empty());

        programRunner.runProgram(plate_pipeline.program,plate_features_text.getTextureID(), WIDTH,HEIGHT,(prog) -> {
            int locCounts = GL20.glGetUniformLocation(prog, "iNeighbors");
            GL20.glUniform1iv(locCounts, GPU_plate_data.getRight());

            // Send neighbors
            int locNeighbors = GL20.glGetUniformLocation(prog, "iLookUp");
            GL20.glUniform1iv(locNeighbors, GPU_plate_data.getLeft());

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + 1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,plate_id_text.getTextureID());
            int locIDTexture = GL20.glGetUniformLocation(prog, "iIDTexture");
            GL20.glUniform1i(locIDTexture, 1);
        });*/

    }

    /*public VulkanTexture2D get_continental_texture(){
        return plate_features_text;
    }

    private Pair<int[], int[]> generate_gpu_plate_data(){

        int[] plate_look_up = new int[plates.size() * 3];
        int i = 0;
        int neighbours_count = 0;

        //flaten plates into a single vector
        for (Map.Entry<Vec2i, Plate> entry : plates.entrySet()) {
            //i is the plate counter. i * 3 because each plate needs 3 ints
            plate_look_up[i * 3] = entry.getKey().x;
            plate_look_up[i * 3 + 1] = entry.getKey().y;
            plate_look_up[i * 3 + 2] = i + neighbours_count;

            neighbours_count += entry.getValue().neighbours.size();
            i += 1;
        }
        int[] neightbours = new int[neighbours_count * 2 + plates.size()];
        i = 0;
        neighbours_count = 0;
        for (Map.Entry<Vec2i, Plate> entry : plates.entrySet()) {
            neightbours[i + neighbours_count] = entry.getValue().neighbours.size();
            int j = 0;
            Iterator<Vec2i> it = entry.getValue().neighbours.iterator();
            while (it.hasNext()) {
                // j is the plate specific neightbour counter; j * 2 because each neibours needs 2 ints
                Vec2i v = it.next();
                neightbours[i + neighbours_count + j * 2] = v.x;
                neightbours[i + neighbours_count + j  * 2 + 1] = v.y;
                j += 1;
            }
            neighbours_count += entry.getValue().neighbours.size();
            i += 1;
        }
        return new Pair(plate_look_up, neightbours);
    }

    class Vec2i{
        public int x,y;
        Vec2i(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public long getHash(){
            return ((long)x << 32) | (y & 0xFFFFFFFFL);
        }
    }

    class Plate{
        //public final Vec2 pos;
        public final Set<Vec2i> neighbours;

        Plate() {
            //this.pos = pos;
            this.neighbours = new HashSet<>();
        }
    }

    private void fill_plates(){
        IntBuffer buffer = BufferUtils.createIntBuffer(WIDTH * HEIGHT * 2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, plate_id_text.getTextureID());

        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL30.GL_RG, GL11.GL_INT, buffer);
        LOGGER.info("heightmap numbers: ");

        Vec2i[] neighbors = new Vec2i[] {
                new Vec2i(0,0), // up
                new Vec2i(0,0), // down
                new Vec2i(0,0), // left
                new Vec2i(0,0)  // right
        };

        for (int y = 0; y < HEIGHT; y++){
            for (int x = 0; x < WIDTH; x += 2){
                Vec2i pos = new Vec2i(buffer.get(y * WIDTH + x),buffer.get(y * WIDTH + x + 1));
                Plate plate = plates.computeIfAbsent(pos, p -> new Plate());
                // Check the 4 neighbors: up, down, left, right
                neighbors[0].x = pos.x;
                neighbors[0].y = pos.y - 1;
                neighbors[1].x = pos.x;
                neighbors[1].y = pos.y + 1;
                neighbors[2].x = pos.x - 1;
                neighbors[2].y = pos.y;
                neighbors[3].x = pos.x + 1;
                neighbors[3].y = pos.y;

                for (Vec2i nPos : neighbors) {
                    // bounce/check bounds
                    if (nPos.x >= 0 && nPos.x < WIDTH && nPos.y >= 0 && nPos.y < HEIGHT) {
                        Plate neighborPlate = plates.get(nPos);
                        if (neighborPlate != null) {
                            plate.neighbours.add(nPos);
                            neighborPlate.neighbours.add(pos); // add back-reference
                        }
                    }
                }
            }
        }
    }
    private class PlateLookup{
        Vec2i pos;
        int index;

        PlateLookup(Vec2i pos, int index){
            this.pos = pos;
            this.index = index;
        }
    }
    */

}

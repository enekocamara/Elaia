package net.enhalo.elaia.vulkan;

import net.enhalo.elaia.Elaia;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.Shaderc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ShaderCompiler {
    static public ByteBuffer compileToByte(String path) throws IOException, RuntimeException{
        String src = loadShaderSource(path + "/shader.comp");
        Elaia.LOGGER.info(src);


        long compiler = Shaderc.shaderc_compiler_initialize();
        long options = Shaderc.shaderc_compile_options_initialize();
        Shaderc.shaderc_compile_options_set_target_env(
                options,
                Shaderc.shaderc_target_env_vulkan,
                Shaderc.shaderc_env_version_vulkan_1_2
        );
        Shaderc.shaderc_compile_options_set_source_language(
                options,
                Shaderc.shaderc_source_language_glsl
        );
        long result = Shaderc.shaderc_compile_into_spv(
                compiler,
                src,
                Shaderc.shaderc_glsl_compute_shader,
                "shader.comp",
                "main",
                options
        );

        if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
            throw new RuntimeException(Shaderc.shaderc_result_get_error_message(result));
        }

        ByteBuffer spv = Shaderc.shaderc_result_get_bytes(result);
        Shaderc.shaderc_result_release(result);
        Shaderc.shaderc_compiler_release(compiler);
        return spv;
    }
    static private String loadShaderSource(String path) throws IOException {
        InputStream stream = Elaia.class.getResourceAsStream(path);
        if (stream == null) throw new FileNotFoundException("Shader not found: " + path);
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
}

package net.enhalo.elaia;

import java.lang.foreign.MemorySegment;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

record BlockHandles(VarHandle type, VarHandle state, VarHandle light) {}

public class Block {
    private static final MemoryLayout BLOCK_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_SHORT.withName("type"),
            ValueLayout.JAVA_BYTE.withName("state"),
            ValueLayout.JAVA_BYTE.withName("light"),
            ValueLayout.JAVA_INT.withName("sublocks") // example padding or data
    );
    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;
    public static final int SIZE_Z = 8;
    SubBlock[]  subblocks = SIZE_X * SIZE_Y * SIZE_Z; //8*8*8*sublocksize
    BlockType[] types = blockType[4];//10bits
    int[] FaceType = int[8*8*16];//4bits each
    bool opaque;//bit
    bool homogeneous;//1bit
    bool full;//1bit


    public static class SubBlock{
        int index;//2bit
        bool smooth;//1bit
        bool empty;//1bit
    }
}

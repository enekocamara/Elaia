package test;

import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import net.enhalo.elaia.BlockMemory;

public class TestPanama {
    /*private static final MemoryLayout BLOCK_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_BYTE.withName("flags"),//1
            MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_BYTE).withName("block_types"),//5
            MemoryLayout.sequenceLayout(8 * 8 * 9 * 2 / 4, ValueLayout.JAVA_BYTE).withName("face_states"),288
            MemoryLayout.sequenceLayout(8 * 8 * 8 / 2, ValueLayout.JAVA_BYTE).withName("subblocks"),256
            ValueLayout.JAVA_INT.withName("block_type_count"),4
            ValueLayout.JAVA_BYTE.withName("default_state_for_type")1
    );*/

    public static void main(String[] args) {
        //int count = 10; // 10 blocks
        long totalBytes = BlockMemory.BLOCK_LAYOUT.byteSize();

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment memory = arena.allocate(totalBytes);
            System.out.println("Allocated " + totalBytes + " bytes for " + 1+ " blocks.");
            System.out.println("Allocated " + totalBytes * 16 * 16 * 16 / 1024.0 / 1024.0 + " mb for " + 16 * 16 * 16 + " blocks.");
            System.out.println("Allocated " + totalBytes * 16 * 16 * 16 / 1024.0 / 1024.0 + " mb for " + 16 * 16 * 16 + " blocks.");


            // Get a view of block 0
            //MemorySegment block0 = memory.asSlice(0, BlockMemory.BLOCK_LAYOUT.byteSize());

            // Access field "flags"
            //VarHandle FLAGS = BlockMemory.BLOCK_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("flags"));
            //FLAGS.set(block0, (byte) 42);

            //byte val = (byte) FLAGS.get(block0);
            //System.out.println("Block 0 flag = " + val);
        }
    }
}
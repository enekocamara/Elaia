package net.enhalo.elaia;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

record BlockHandles(VarHandle type, VarHandle state, VarHandle light) {}

public class BlockMemory {
    public static final MemoryLayout BLOCK_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("block_type_count"),
            ValueLayout.JAVA_SHORT.withName("default_state_for_type"),
            ValueLayout.JAVA_BYTE.withName("flags"),
            MemoryLayout.sequenceLayout(5, ValueLayout.JAVA_BYTE).withName("block_types"), //8*5 = 40
            MemoryLayout.sequenceLayout(8*8*9*2/2, ValueLayout.JAVA_BYTE).withName("face_states"),
            // instead what if it has an index to a local space hashmap (16*16*16) or a blocktype hasmap. The key to
            // the hashmap would be the type of block as well as the 'state'. Then the entry of that hashmap would have
            // all the steps or the true 'state' so if u have a state for a subblock u go to hashmap it gives u a index.
            // That way if a state takes 10 bits and there are 100 000 faces on the screen that use it instead of
            // needing 1 000 000 bits we need 10 bits and the index (block pos)
            // subblock id 9bits, face dir 3bits . short 16 bits
            //
            MemoryLayout.sequenceLayout(8*8*8/2, ValueLayout.JAVA_BYTE).withName("subblocks")
    );

    public static VarHandle FLAGS_HANDLE = BLOCK_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("flags"));
    public static VarHandle BLOCK_TYPE_COUNT_HANDLE = BLOCK_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("block_type_count"));
    public static VarHandle DEFAULT_STATE_HANDLE = BLOCK_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("default_state_for_type"));

    public static final byte FLAG_CUSTOM_BLOCK = 1 << 0;  // 0000_0001
    public static final byte FLAG_OPAQUE       = 1 << 1;  // 0000_0010
    public static final byte FLAG_HOMOGENEOUS  = 1 << 2;  // 0000_0100
    public static final byte FLAG_FULL         = 1 << 3;  // 0000_1000
    public static final byte FLAG_VISIBLE      = 1 << 4;  // 0001_0000
    public static final byte FLAG_TRANSPARENCY = 1 << 5;  // 0010_0000
    //public static final byte FLAG_TRANSPARENCY = 1 << 5;  // 0100_0000




    private static void setFlag(MemorySegment block, byte mask, boolean value) {
        byte flags = (byte) FLAGS_HANDLE.get(block);
        if (value)
            flags |= mask;   // set bit
        else
            flags &= ~mask;  // clear bit
        FLAGS_HANDLE.set(block, flags);
    }
    private static boolean getFlag(MemorySegment block, byte mask) {
        byte flags = (byte) FLAGS_HANDLE.get(block);
        return (flags & mask) != 0;
    }

    public static boolean isCustomBlock(MemorySegment block){
        return getFlag(block, FLAG_CUSTOM_BLOCK);
    }
    public static boolean isOpaque(MemorySegment block){
        return getFlag(block, FLAG_OPAQUE);
    }
    public static boolean isHomogeneous(MemorySegment block){
        return getFlag(block, FLAG_HOMOGENEOUS);
    }
    public static boolean isFULL(MemorySegment block){
        return getFlag(block, FLAG_FULL);
    }
    public static boolean isVisible(MemorySegment block){
        return getFlag(block, FLAG_VISIBLE);
    }
    public static boolean isTransparent(MemorySegment block){
        return getFlag(block, FLAG_TRANSPARENCY);
    }



    public static void setIsCustomBlock(MemorySegment block, boolean value){
        setFlag(block, FLAG_CUSTOM_BLOCK, value);
    }
    public static void setIsOpaque(MemorySegment block, boolean value){
        setFlag(block, FLAG_OPAQUE, value);
    }
    public static void setIsHomogeneous(MemorySegment block, boolean value){
        setFlag(block, FLAG_HOMOGENEOUS, value);
    }
    public static void setIsFULL(MemorySegment block, boolean value){
        setFlag(block, FLAG_FULL, value);
    }
    public static void setIsVisible(MemorySegment block, boolean value){
        setFlag(block, FLAG_VISIBLE, value);
    }
    public static void setIsTransparent(MemorySegment block, boolean value){
        setFlag(block, FLAG_TRANSPARENCY, value);
    }



    private static final int MASK_9BIT = 0x1FF; // 9 bits set (511 decimal)

    public static int getType1BlockCount(MemorySegment block) {
        int packed = (int) BLOCK_TYPE_COUNT_HANDLE.get(block);
        return packed & MASK_9BIT;
    }

    public static int getType2BlockCount(MemorySegment block) {
        int packed = (int) BLOCK_TYPE_COUNT_HANDLE.get(block);
        return (packed >> 9) & MASK_9BIT;
    }

    public static int getType3BlockCount(MemorySegment block) {
        int packed = (int) BLOCK_TYPE_COUNT_HANDLE.get(block);
        return (packed >> 18) & MASK_9BIT;
    }


    private static final short DEFAULT_TYPE_MASK = 0b1111;

    public static short getDefaultState(MemorySegment block, int index) {
        short packed = (short) DEFAULT_STATE_HANDLE.get(block);
        return (short) ((packed >> (index * 4)) & DEFAULT_TYPE_MASK);
    }
    public static void setDefaultType(MemorySegment block, int index, short value) {
        short packed = (short) DEFAULT_STATE_HANDLE.get(block);
        int shift = index * 4;
        // Clear the old 4 bits
        packed &= ~(DEFAULT_TYPE_MASK << shift);
        // Set the new 4 bits
        packed |= (value & DEFAULT_TYPE_MASK) << shift;
        DEFAULT_STATE_HANDLE.set(block, packed);
    }


    private final Arena arena;
    private final MemorySegment base;
    private final long structSize;
    private final int count;

    public BlockMemory(int count) {
        this.arena = Arena.ofShared();
        this.structSize = BLOCK_LAYOUT.byteSize();
        this.count = count;
        this.base = arena.allocate(this.structSize * count);
    }


    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;
    public static final int SIZE_Z = 8;
    /*
    SubBlock[]  subblocks = SIZE_X * SIZE_Y * SIZE_Z; //8*8*8*sublocksize
        |BlockType[] types = blockType[3];//30bits
        |BlockType custom_type; //10 bits
    int[] FaceType = int[8*8*16];//4bits each

    int block_type_count;9 bits each
    byte default_state_for_type; //2bits each 6 total
    bool custom_block;//1bit it will use the first blocktype
    bool opaque;//bit
    bool homogeneous;//1bit
    bool full;//1bit
    bool visible; //1bit
    bool transpacency; 1bit


    public static class SubBlock{
        int index;//2bit
        bool smooth;//1bit
        bool empty;//1bit
    }*/
}

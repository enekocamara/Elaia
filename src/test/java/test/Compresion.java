package test;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Vector;


public class Compresion {
    short key;
    short value;

    Vector<Short> values;
    HashMap<Short, Integer> keys;

    void insert(short key, short value){
        values.add(value);
    }

    short get(short key){
        int index;
        return values.get(index);
    }

    public class BoundingBox{
        Short point1;
        Short point2;
        Short point3;
    }

    public class State{}

    public class Octree{
        public Vector<Node> nodestate;
        public Vector<Node> nodestype;
        HashMap<Integer, State> statesIDvalue;
        HashMap<Integer, State> typeIDvalue;
        public Vector2i location = new Vector2i(0,0,0);
        public Vector3i bot = new Vector3i(0,0,0);


        public class Node{
            Vector<Integer> childs;
            boolean isLeaf;
            int valueID;
        }

        public set(Vec3i postion, int value){

        }
    }
}

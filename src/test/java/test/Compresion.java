package test;

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

        public class Node{
            Vector<Integer> childs;
            boolean isLeaf;
            int valueID;
        }

        void insert(short post, short value){

        }

        void insert(short pos, short value){

        }
    }
}

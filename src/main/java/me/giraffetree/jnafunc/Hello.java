package me.giraffetree.jnafunc;

import com.sun.jna.*;
import lombok.Data;

/**
 * @author GiraffeTree
 * @date 2019/10/25
 */
public class Hello {

    public interface JnaLibrary extends Library {

        // JNA 为 dll 名称
        JnaLibrary INSTANCE = Native.load("JNA", JnaLibrary.class);

        int max(int a, int b);

        void getBool(boolean x);

        void testArray(short[] vals, int len);

        void testStruct(ArrInfo arrInfo);

        void printUser(User.ByValue user);

        void printUserRef(User user);

        @Data
        @Structure.FieldOrder({"name", "height", "weight"})
        public static class User extends Structure {

            public static class UserValue extends User implements Structure.ByValue {

                public UserValue(String name, int height, double weight) {
                    super(name, height, weight);
                }
            }

            public User(String name, int height, double weight) {
                this.name = name;
                this.height = height;
                this.weight = weight;
            }

            public String name;

            public int height;

            public double weight;
        }


        @Data
        @Structure.FieldOrder({"vals", "len"})
        public static class ArrInfo extends Structure {
            public Pointer vals;
            public int len;

            public ArrInfo(Pointer vals, int len) {
                this.vals = vals;
                this.len = len;
            }
        }

    }

    public static void main(String[] args) {
        testBool();
        testMax();
        testArray();
        testUser();
        testPointer();
    }

    public static void testBool() {
        // c++ output:
        // bool: 255 in true
        JnaLibrary.INSTANCE.getBool(true);
    }

    public static void testMax() {
        int max = JnaLibrary.INSTANCE.max(100, 200);
        // out: 200
        System.out.println(max);
    }

    public static void testArray() {
        JnaLibrary.INSTANCE.testArray(new short[]{1, 2, 3, 4}, 4);
    }

    public static void testUser() {
        JnaLibrary.User.UserValue user1 = new JnaLibrary.User.UserValue("user1", 186, 65.2);
        JnaLibrary.INSTANCE.printUserRef(user1);
        JnaLibrary.INSTANCE.printUser(user1);
        // out:
//        printUserRef user: user1 height: 186 weight: 65.20
//        printUser user: user1 height: 186 weight: 65.20
    }


    public static void testPointer() {
        // java main test
        int len = 3;
        int shortSize = Native.getNativeSize(Short.class);
        Pointer pointer = new Memory(len * shortSize);
        for (int i = 0; i < len; i++) {
            pointer.setShort(shortSize * i, (short) i);
        }
        JnaLibrary.ArrInfo arrInfo = new JnaLibrary.ArrInfo(pointer, len);
        JnaLibrary.INSTANCE.testStruct(arrInfo);
// out
//        arrInfo[0]: 0
//        arrInfo[1]: 1
//        arrInfo[2]: 2
    }


}

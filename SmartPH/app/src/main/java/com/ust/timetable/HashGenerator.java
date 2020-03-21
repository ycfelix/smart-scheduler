package com.ust.timetable;

public class HashGenerator {

    //default hashtable for 8bit pearson hash
    private static int[] hashtable={183, 236, 198, 24, 173, 169, 187, 212, 233, 160, 161,
            7, 119, 131, 89, 254, 204, 86, 115, 58, 97, 9, 163, 147, 108, 18, 57,
            48, 67, 200, 92, 140, 171, 249, 230, 47, 3, 172, 125, 49, 100, 179, 170,
            231, 151, 104, 197, 56, 152, 78, 127, 15, 245, 168, 21, 116, 222, 33, 80,
            81, 61, 101, 123, 253, 87, 176, 252, 162, 74, 31, 52, 46, 73, 247, 40,
            211, 229, 206, 208, 214, 219, 220, 150, 250, 77, 94, 186, 107, 66, 194,
            59, 215, 88, 43, 192, 42, 143, 191, 190, 95, 180, 85, 79, 91, 25, 181,
            185, 76, 63, 122, 221, 145, 30, 178, 213, 167, 32, 84, 12, 129, 27, 239,
            2, 120, 203, 246, 241, 114, 60, 210, 188, 244, 158, 55, 90, 155, 134, 139,
            93, 83, 70, 128, 234, 255, 110, 82, 54, 209, 65, 199, 102, 243, 174, 109,
            34, 159, 103, 96, 202, 235, 22, 62, 189, 184, 13, 8, 137, 156, 141, 113,
            10, 175, 177, 142, 248, 28, 44, 53, 166, 138, 17, 14, 164, 153, 0, 112,
            165, 16, 133, 41, 68, 216, 39, 37, 201, 20, 149, 205, 51, 98, 45, 126, 193,
            146, 106, 232, 121, 75, 35, 225, 237, 130, 105, 207, 64, 135, 228, 69, 71, 226,
            196, 117, 227, 5, 218, 154, 4, 23, 195, 19, 111, 240, 29, 136, 36, 1, 124, 224,
            132, 217, 182, 238, 251, 11, 148, 72, 242, 99, 223, 50, 157, 118, 38, 6, 144, 26};

    public static String toHashCode(String name){
        int hash=name.length()%256;
        for(int i:name.toCharArray()){
            hash=hashtable[hash ^ Character.getNumericValue(i)];
        }
        return String.valueOf(hash);
    }
}
package com.example.main.boj.start1.math;

import java.io.*;

//8진수 2진수
public class P1212 {
    private static final String[] first = {
            "0", "1", "10", "11", "100", "101", "110", "111"
    };

    private static final String[] temp = {
            "000", "001", "010", "011", "100", "101", "110", "111"
    };

    public static void main (String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        char[] num = br.readLine().toCharArray();
        bw.write(first[num[0] - 48]);
        for (int i = 1; i < num.length; i++) {
            bw.write(temp[num[i] - 48]);
        }
        br.close();
        bw.flush();
        bw.close();
    }
}

package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class P17413 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        String str = br.readLine();

        StringBuilder sb = new StringBuilder();
        boolean reverse = true;
        char c = 0;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '<') {
                bw.write(sb.reverse().toString());
                sb = new StringBuilder();
                reverse = false;
                sb.append(c);
            } else if (c == '>') {
                sb.append(c);
                bw.write(sb.toString());
                sb = new StringBuilder();
                reverse = true;
            } else if (c == ' ') {
                if (!reverse) {
                    sb.append(c);
                } else {
                    bw.write(sb.reverse().toString());
                    bw.write(c);
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }
        if (c != '>') {
            bw.write(sb.reverse().toString());
        } else {
            bw.write(sb.toString());
        }
        bw.flush();
        bw.close();
    }
}

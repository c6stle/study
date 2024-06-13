package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//ROT13
public class P11655 {
    public static void main(String[] args) throws Exception {
        //System.out.println('z' - 13); //109
        //System.out.println('A' - 0); //65
        //System.out.println('Z' - 0); //90
        //System.out.println('Z' - 13); //77

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'a' && c <= 'z') {
                char rot13c; //13 글자 밀기
                if (c > 109) {
                    rot13c = (char) (c - 13); //z = 122
                } else {
                    rot13c = (char) (c + 13);
                }
                sb.append(rot13c);
            } else if (c >= 'A' && c <= 'Z') {
                char rot13c;
                if (c > 77) {
                    rot13c = (char) (c - 13); //Z = 90
                } else {
                    rot13c = (char) (c + 13);
                }
                sb.append(rot13c);
            } else {
                sb.append(c);
            }
        }
        System.out.println(sb);
    }
}

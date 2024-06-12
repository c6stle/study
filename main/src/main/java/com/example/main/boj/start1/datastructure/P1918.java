package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

//후위표기식
public class P1918 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String func = br.readLine();

        Queue<Character> val = new ArrayDeque<>();
        Stack<Character> calc = new Stack<>();
        for (int i = 0; i < func.length(); i++) {
            char c = func.charAt(i);
            val.offer(c);

        }


    }
}

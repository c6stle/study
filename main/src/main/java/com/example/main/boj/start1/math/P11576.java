package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.StringTokenizer;

public class P11576 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int a = Integer.parseInt(st.nextToken());
        int b = Integer.parseInt(st.nextToken());

        int m = Integer.parseInt(br.readLine());

        int num = 0;
        st = new StringTokenizer(br.readLine());
        for (int i = m - 1; i >= 0; i--) {
            num += Integer.parseInt(st.nextToken()) * Math.pow(a, i);
        }

        Stack<Integer> stack = new Stack<>();
        while (num > 0) {
            stack.push(num % b);
            num /= b;
        }
        while (!stack.isEmpty()) {
            System.out.print(stack.pop() + " ");
        }
    }
}

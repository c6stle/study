package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

//후위표기식
public class P1935 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(br.readLine());
        String str = br.readLine();
        double[] arr = new double[n];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = Double.parseDouble(br.readLine());
        }

        Stack<Double> stack = new Stack<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                stack.push(arr[c - 'A']);
            } else {
                if (!stack.isEmpty()) {
                    double first = stack.pop();
                    double second = stack.pop();
                    if (c == '*') {
                        stack.push(first * second);
                    } else if (c == '/') {
                        stack.push(second / first);
                    } else if (c == '+') {
                        stack.push(second + first);
                    } else if (c == '-') {
                        stack.push(second - first);
                    }
                }
            }
        }
        System.out.printf("%.2f", stack.pop());
        br.close();
    }
}


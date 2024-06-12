package com.example.main.boj.start1.datastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

//후위표기식
public class P1918 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String func = br.readLine();

        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < func.length(); i++) {
            char c = func.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                sb.append(c);
            } else if (c != '(' && c != ')') {
                while (!stack.isEmpty() && priority(c) <= priority(stack.peek())) {
                    sb.append(stack.pop());
                }
                stack.push(c);
            } else if (c == '(') {
                stack.push(c);
            } else {
                while (stack.peek() != '(') {
                    sb.append(stack.pop());
                }
                stack.pop(); //여는 괄호 제거
            }
        }
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }
        System.out.println(sb);
    }

    public static int priority(char c) {
        if (c == '*' || c == '/') {
            return 2;
        } else if (c == '+' || c == '-') {
            return 1;
        } else {
            return 0;
        }
    }
}

package com.example.main.boj.기본1;

import java.util.Scanner;
import java.util.Stack;

//스택수열
public class P1874 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        StringBuilder sb = new StringBuilder();

        Stack<Integer> stack = new Stack<>();
        int num = 1;
        boolean flag = true;
        for (int i = 0; i < n; i++) {
            int l = sc.nextInt();
            if (l >= num) {
                while (l >= num) {
                    stack.push(num);
                    num++;
                    sb.append("+\n");
                }
                stack.pop();
                sb.append("-\n");
            } else {
                int tmp = stack.pop();
                if (tmp > l) {
                    System.out.println("NO");
                    flag = false;
                    break;
                } else {
                    sb.append("-\n");
                }
            }
        }
        if (flag) {
            System.out.println(sb);
        }
    }
}

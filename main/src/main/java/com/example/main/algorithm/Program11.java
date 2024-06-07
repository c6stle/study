package com.example.main.algorithm;

import java.io.*;
import java.util.*;

//스택으로 오름차순 수열 만들기(스택구조) 1874
public class Program11 {

    public static void doing() throws IOException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] arr = new int[n];

        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }

        Stack<Integer> stack = new Stack<>();
        StringBuffer bf = new StringBuffer();
        int num = 1;
        boolean result = true;
        for (int su : arr) {
            if (su >= num) {
                while (su >= num) {
                    stack.push(num++);
                    bf.append("+\n");
                }
                stack.pop();
                bf.append("-\n");
            } else {
                int tempN = stack.pop();
                if (tempN > su) {
                    System.out.println("NO");
                    result = false;
                    break;
                } else {
                    bf.append("-\n");
                }
            }
        }
        if (result) {
            System.out.println(bf);
        }
    }
}

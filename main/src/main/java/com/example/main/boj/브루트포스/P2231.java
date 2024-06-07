package com.example.main.boj.브루트포스;

import java.util.Scanner;

public class P2231 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String n = sc.next();
        int num = Integer.parseInt(n);
        int result = 0;
        for (int i = 0; i < num; i++) {
            String strNum = String.valueOf(i);
            int sum = 0;
            for (int j = 0; j < strNum.length(); j++) {
                sum += strNum.charAt(j) - '0';
            }
            if (i + sum == num) {
                result = i;
                break;
            }
        }
        System.out.println(result);
    }
}

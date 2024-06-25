package com.example.main.boj.start2.bruteforce;

import java.util.Scanner;

//리모컨
public class P1107 {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int target = sc.nextInt();
        int now = 100;
        boolean[] broke = new boolean[10];
        int size = sc.nextInt();
        for (int i = 0; i < size; i++) {
            broke[sc.nextInt()] = true;
        }

        int result = Math.abs(target - now);

        for (int i = 0; i < 1000000; i++) {
            String num = String.valueOf(i);

            boolean tf = false;
            for (int j = 0; j < num.length(); j++) {
                if (broke[num.charAt(j) - '0']) {
                    tf = true;
                    break;
                }
            }
            if (!tf) {
                int min = Math.abs(target - i) + num.length();
                result = Math.min(min, result);
            }
        }
        System.out.println(result);
    }
}

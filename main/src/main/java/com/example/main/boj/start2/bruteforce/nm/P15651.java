package com.example.main.boj.start2.bruteforce.nm;

import java.util.Scanner;

//N과 M (3)
public class P15651 {
    static StringBuilder sb = new StringBuilder();
    static int[] result;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();

        result = new int[m];

        nm(n, m, 0);

        System.out.println(sb);
    }

    static void nm(int n, int m, int depth) {
        if (m == depth) {
            for (int num : result) {
                sb.append(num).append(" ");
            }
            sb.append("\n");
            return;
        }

        for (int i = 1; i <= n; i++) {
            result[depth] = i;
            nm(n, m, depth + 1);
        }
    }
}

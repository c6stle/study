package com.example.main.boj.start2.bruteforce.nm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

//N과 M (8)
public class P15657 {

    static int[] arr;
    static int[] result;
    static StringBuilder sb = new StringBuilder();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }
        Arrays.sort(arr);
        result = new int[m];

        nm(1, n, m, 0);

        System.out.println(sb);
    }

    static void nm(int s, int n, int m, int depth) {
        if (m == depth) {
            for (int num : result) {
                sb.append(num).append(" ");
            }
            sb.append("\n");
            return;
        }

        for (int i = s; i <= n; i++) {
            result[depth] = arr[i - 1];
            nm(i, n, m, depth + 1);
        }
    }
}

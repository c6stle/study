package com.example.main.boj.start2.bruteforce.nm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

//N과 M (12)
public class P15666 {
    static int[] arr;
    static int[] result;
    static Set<String> set;

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
        set = new LinkedHashSet<>();

        nm(1, n, m, 0);

        set.forEach(System.out::println);
    }

    static void nm(int s, int n, int m, int depth) {
        if (m == depth) {
            StringBuilder sb = new StringBuilder();
            for (int num : result) {
                sb.append(num).append(" ");
            }
            set.add(sb.toString());
            return;
        }

        for (int i = s; i <= n; i++) {
            result[depth] = arr[i - 1];
            nm(i, n, m, depth + 1);
        }
    }
}

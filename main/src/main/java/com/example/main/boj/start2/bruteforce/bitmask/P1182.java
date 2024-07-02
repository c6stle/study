package com.example.main.boj.start2.bruteforce.bitmask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//부분수열의 합
public class P1182 {

    static int n;
    static int s;
    static int cnt;
    static int[] arr;

    //비트연산자 풀이
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        s = Integer.parseInt(st.nextToken());

        arr = new int[n];
        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        for (int i = 1; i < (1 << n); i++) {
            int sum = 0;
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    sum += arr[j];
                }
            }
            if (sum == s) {
                cnt += 1;
            }
        }
        System.out.println(cnt);
    }

    //dfs 풀이
    //public static void main(String[] args) throws Exception {
    //    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    //    StringTokenizer st = new StringTokenizer(br.readLine());
    //
    //    n = Integer.parseInt(st.nextToken());
    //    s = Integer.parseInt(st.nextToken());
    //
    //    arr = new int[n];
    //    st = new StringTokenizer(br.readLine());
    //    for (int i = 0; i < n; i++) {
    //        arr[i] = Integer.parseInt(st.nextToken());
    //    }
    //
    //    dfs(0, 0);
    //    System.out.println(s == 0 ? cnt - 1 : cnt);
    //}
    //
    //static void dfs(int idx, int sum) {
    //    if (idx == n) {
    //        if (sum == s) {
    //            cnt++;
    //        }
    //        return;
    //    }
    //    dfs(idx + 1, sum + arr[idx]);
    //    dfs(idx + 1, sum);
    //}
}

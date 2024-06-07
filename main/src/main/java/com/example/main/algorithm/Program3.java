package com.example.main.algorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//구간합 11659
public class Program3 {
    public static void doing() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int qNum = Integer.parseInt(st.nextToken());

        long[] arr = new long[n+1];
        st = new StringTokenizer(br.readLine());

        long[] sumArr = new long[n+1];
        for (int i = 1; i <= n; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
            sumArr[i] += sumArr[i - 1] + arr[i];
        }

        for (int i = 0; i < qNum; i++) {
            st = new StringTokenizer(br.readLine());
            int startIdx = Integer.parseInt(st.nextToken());
            int endIdx = Integer.parseInt(st.nextToken());
            System.out.println(sumArr[endIdx] - sumArr[startIdx-1]);
        }
    }
}

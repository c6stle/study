package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

//조합 0의 개수
public class P2004 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        int count5 = getComb(n, 5) - getComb(n - m, 5) - getComb(m, 5);
        int count2 = getComb(n, 2) - getComb(n - m, 2) - getComb(m, 2);

        System.out.println(Math.min(count2, count5));
    }

    static int getComb(int n, int k){ //k = 2 or 5
        int count = 0;
        while (n >= k) {
            count += (n / k);
            n /= k;
        }
        return count;
    }
}

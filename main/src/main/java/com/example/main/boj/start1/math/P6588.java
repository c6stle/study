package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//골드바흐의 추측(소수 찾기, 더하기)
public class P6588 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        //소수 배열을 미리 생성함 : false 가 소수
        boolean[] arr = new boolean[1_000_001];
        arr[0] = true;
        arr[1] = true;
        //에라토스테네스의 체
        for (int i = 2; i * i < arr.length; i++) {
            if (!arr[i]) {
                for (int j = i * i; j < arr.length; j += i) {
                    arr[j] = true;
                }
            }
        }

        int n;
        StringBuilder sb = new StringBuilder();
        while ((n = Integer.parseInt(br.readLine())) != 0) {
            int num1 = 0;
            int num2 = 0;
            for (int i = 2; i <= n; i++) {
                num1 = i;
                num2 = n - i;
                // num1 + num2 = n
                if (!arr[num1] && !arr[num2]) {
                    bw.write(n + " = " + num1 + " + " + num2 + "\n");
                    break;
                }
            }
            if (num1 == n && num2 == 0) {
                bw.write("Goldbach's conjecture is wrong.\n");
            }
        }
        bw.flush();
        bw.close();
    }
}

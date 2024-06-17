package com.example.main.boj.start1.math;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

//골드바흐 파티션
public class P17103 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        int n = Integer.parseInt(br.readLine());

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

        for (int j = 0 ; j < n; j++) {
            int number = Integer.parseInt(br.readLine());
            int cnt = 0;
            for (int i = 2; i < number/2 + 1; i ++) {
                if (!arr[i] && !arr[number - i]) {
                    cnt++;
                }
            }
            bw.write(String.valueOf(cnt));
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    }
}

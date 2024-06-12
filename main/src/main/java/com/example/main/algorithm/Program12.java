package com.example.main.algorithm;

import java.io.*;
import java.util.Stack;

//오큰수 구하기(스택구조) 17298
public class Program12 {

    public static void doing() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(bf.readLine());
        int[] arr = new int[n];
        int[] ans = new int[n];

        String[] s = bf.readLine().split(" ");
        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(s[i]);
        }

        Stack<Integer> myStack = new Stack<>();
        myStack.push(0);
        for (int i = 1; i < n; i++) {
            while (!myStack.isEmpty() && arr[myStack.peek()] < arr[i]) {
                ans[myStack.pop()] = arr[i];
            }
            myStack.push(i);
        }
        while (!myStack.empty()) {
            ans[myStack.pop()] = -1;
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        for (int i = 0; i < n; i++) {
            bw.write(ans[i] + " ");
        }
        bw.write("\n");
        bw.flush();
        bw.close();
    }
}

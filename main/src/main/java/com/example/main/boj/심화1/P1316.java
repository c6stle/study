package com.example.main.boj.심화1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class P1316 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int wordCnt = Integer.parseInt(br.readLine());

        int groupWordCnt = 0;
        for (int i = 0; i < wordCnt; i++) {
            Set<Character> charSet = new HashSet<>();

            String word = br.readLine();

            boolean temp = true;
            for (int j = 0; j<word.length(); j++) {
                char bc;
                if (j == 0) {
                    bc = word.charAt(j);
                } else {
                    bc = word.charAt(j - 1);
                }

                char c = word.charAt(j);

                if (bc != c) {
                    if (charSet.contains(c)) {
                        temp = false;
                        break;
                    } else {
                        charSet.add(c);
                    }
                } else {
                    charSet.add(c);
                }
            }
            if (temp) {
                groupWordCnt++;
            }
        }
        System.out.println(groupWordCnt);
    }
}

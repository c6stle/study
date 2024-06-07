package com.example.main.boj.심화1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class P2941 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = br.readLine();

        int word = 0;
        for (int i = 0; i < str.length(); i++) {

            char c = str.charAt(i);

            if (c == 'c') {
                if(i < str.length() - 1) {
                    if ('=' == str.charAt(i + 1)) {
                        i++;
                    } else if ('-' == str.charAt(i + 1)) {
                        i++;
                    }
                }
            } else if (c == 'd') {
                if(i < str.length() - 1) {
                    if(str.charAt(i + 1) == 'z') {
                        if(i < str.length() - 2) {
                            if(str.charAt(i + 2) == '=') {	// dz= 일 경우
                                i += 2;
                            }
                        }
                    } else if(str.charAt(i + 1) == '-') {	// d- 일 경우
                        i++;
                    }
                }
            } else if(c == 'l') {
                if(i < str.length() - 1) {
                    if(str.charAt(i + 1) == 'j') {	// lj 일 경우
                        i++;
                    }
                }
            } else if(c == 'n') {
                if(i < str.length() - 1) {
                    if(str.charAt(i + 1) == 'j') {	// nj 일 경우
                        i++;
                    }
                }
            } else if(c == 's') {
                if(i < str.length() - 1) {
                    if(str.charAt(i + 1) == '=') {	// s= 일 경우
                        i++;
                    }
                }
            } else if(c == 'z') {
                if(i < str.length() - 1) {
                    if(str.charAt(i + 1) == '=') {	// z= 일 경우
                        i++;
                    }
                }
            }

            word++;
        }

        System.out.println(word);
    }
}

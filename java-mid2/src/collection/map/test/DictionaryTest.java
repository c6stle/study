package collection.map.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DictionaryTest {
    public static void main(String[] args) {
        Map<String, String> dictionary = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("==단어 입력 단계==");
        while (true) {
            System.out.println("영어 단어를 입력하세요 (종료를 'q'): ");
            String englishWord = sc.nextLine();

            if (englishWord.equals("q")) {
                break;
            }

            System.out.println("한글 뜻을 입력하세요: ");
            String koreanMeaning = sc.nextLine();
            dictionary.put(englishWord, koreanMeaning);
        }

        System.out.println("단어 검색 단계");
        while (true) {
            System.out.println("찾을 영어 단어를 입력하세요 (종료는 'q'): ");
            String searchWord = sc.nextLine();
            if (searchWord.equals("q")) {
                break;
            }

            if (dictionary.containsKey(searchWord)) {
                String koreanMeaning = dictionary.get(searchWord);
                System.out.println(searchWord + "의 뜻: " + koreanMeaning);
            }
        }

    }
}

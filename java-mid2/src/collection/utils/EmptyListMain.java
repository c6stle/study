package collection.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmptyListMain {
    public static void main(String[] args) {
        //빈 가변 리스트 생성
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        //빈 불변 리스트 생성
        List<Object> list3 = Collections.emptyList(); //java 5
        List<Integer> list4 = List.of(); //java 9

        System.out.println("list3 = " + list3);
        System.out.println("list4 = " + list4);

        List<Integer> list5 = Arrays.asList(1, 2, 3);
        List<Integer> list6 = List.of(1, 2, 3);

        Integer[] arr = {1,2,3,4,5};
        List<Integer> arrList = Arrays.asList(arr);
        System.out.println("arr = " + Arrays.toString(arr));
        System.out.println("arrList = " + arrList);
    }
}

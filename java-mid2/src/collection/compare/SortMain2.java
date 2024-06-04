package collection.compare;

import java.util.Arrays;
import java.util.Comparator;

public class SortMain2 {
    public static void main(String[] args) {
        Integer[] array = {3, 2, 1};
        System.out.println(Arrays.toString(array));
        System.out.println("Comparator 비교");
        Arrays.sort(array, new AscComparator());
        System.out.println("AscComparator: " + Arrays.toString(array));

        Arrays.sort(array, new DescComparator());
        System.out.println("DescComparator: " + Arrays.toString(array));

        Arrays.sort(array, new AscComparator().reversed()); //DescComparator 와 같음
        System.out.println("AscComparator.reversed(): " + Arrays.toString(array));
    }

    static class AscComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            System.out.println("o1 = " + o1 + " o2 = " + o2);
            // -1 이면 첫번째값이 더 작은것
            return o1.compareTo(o2);
        }
    }
    static class DescComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            System.out.println("o1 = " + o1 + " o2 = " + o2);
            // -1 이면 첫번째값이 더 작은것
            return o1.compareTo(o2) * -1;
        }
    }
}

package collection.array;

import java.io.BufferedWriter;
import java.util.Arrays;

public class MyArrayListV1 {
    private static final int DEFAULT_CAPACITY = 5;

    private Object[] elementData;
    private int size = 0;

    public MyArrayListV1() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    public MyArrayListV1(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }

    public int size() {
        return size;
    }

    public void add(Object obj) {
        elementData[size] = obj;
        size++;
    }

    public Object get(int index) {
        return elementData[index];
    }

    public Object set(int index, Object obj) {
        //기존값 반환
        Object oldValue = get(index);
        elementData[index] = obj;
        return oldValue;
    }

    public int indexOf(Object obj) {
        for (int i = 0; i < size; i++) {
            if (obj.equals(elementData[i])) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        //[1,2,3]
        //Arrays.copyOf(elementData, size) : size 만큼만 출력
        return Arrays.toString(Arrays.copyOf(elementData, size)) +
                " size=" + size +
                ", capacity=" + elementData.length;
    }
}

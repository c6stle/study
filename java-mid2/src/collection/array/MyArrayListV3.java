package collection.array;

import java.util.Arrays;

public class MyArrayListV3 {

    private static final int DEFAULT_CAPACITY = 5;

    private Object[] elementData;
    private int size = 0;

    public MyArrayListV3() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    public MyArrayListV3(int initialCapacity) {
        elementData = new Object[initialCapacity];
    }

    public int size() {
        return size;
    }

    public void add(Object obj) {
        if (size == elementData.length) {
            grow();
        }
        elementData[size] = obj;
        size++;
    }

    //추가부분
    public void add(int index, Object obj) {
        if (size == elementData.length) {
            grow();
        }
        shiftRightFrom(index);
        elementData[index] = obj;
        size++;
    }
    //추가부분, 요소의 마지막부터 index 까지 오른쪽으로 이동
    private void shiftRightFrom(int index) {
        for (int i = size; i > index; i--) {
            elementData[i] = elementData[i - 1];
        }
    }

    private void grow() {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity * 2;
        //배열을 새로 만들고, 기존 배열을 새로운 배열에 복사
        elementData = Arrays.copyOf(elementData, newCapacity);
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

    //추가부분
    public Object remove(int index) {
        Object oldValue = get(index);
        //데이터 이동
        shiftLeftFrom(index);
        size --;
        elementData[size] = null;
        return oldValue;
    }

    private void shiftLeftFrom(int index) {
        for (int i = index; i < size - 1; i++) {
            elementData[i] = elementData[i + 1];
        }
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

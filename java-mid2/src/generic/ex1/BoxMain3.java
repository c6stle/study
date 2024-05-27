package generic.ex1;

public class BoxMain3 {
    public static void main(String[] args) {
        //생성 시점에 T의 타입을 결정
        GenericBox<Integer> integerBox = new GenericBox<Integer>();

        integerBox.set(10);
        //integerBox.set("문자100");
        Integer integer = integerBox.get(); //캐스팅 필요없음
        System.out.println("integer = " + integer);

        GenericBox<String> stringBox = new GenericBox<String>();
        stringBox.set("문자100");
        String str = stringBox.get();
        System.out.println("str = " + str);

        //원하는 모든 타입 사용 가능
        GenericBox<Double> doubleBox = new GenericBox<Double>();
        doubleBox.set(10.5);
        Double aDouble = doubleBox.get();
        System.out.println("aDouble = " + aDouble);

        //타입 추론 : 생성하는 제네릭 타입 생략 가능
        GenericBox<Integer> integerBox2 = new GenericBox<>();
    }
}

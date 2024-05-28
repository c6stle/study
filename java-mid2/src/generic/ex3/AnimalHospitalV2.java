package generic.ex3;

public class AnimalHospitalV2<T> {

    private T animal;

    public void set(T animal) {
        this.animal = animal;
    }

    public void checkup() {
        //T의 타입을 메서드를 정의하는 시점에는 알 수 없음. Object 의 기능만 사용

        animal.toString();
        animal.equals(null);

        //System.out.println("동물 이름: " + animal.getName());
        //System.out.println("동물 크기: " + animal.getSize());
    }

    public T bigger(T target) {
        //return animal.getSize() > target.getSize() ? animal : target;
        return null;
    }

}

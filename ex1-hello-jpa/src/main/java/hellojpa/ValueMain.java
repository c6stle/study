package hellojpa;

public class ValueMain {

    public static void main(String[] args) {
        int a = 10;
        int b = a;

        a = 20;

        Address address1 = new Address("city", "street", "zipcode");
        Address address2 = new Address("city", "street", "zipcode");

        System.out.println("address1 = " + address1.equals(address2));
    }
}

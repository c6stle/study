package collection.map.test.cart;

public class CartMain {
    public static void main(String[] args) {
        //Product hashCode equals 구현 필수!!!
        Cart cart = new Cart();
        cart.add(new Product("사과", 1000), 1);
        cart.add(new Product("바나나", 500), 1);
        cart.printAll();
        cart.add(new Product("사과", 1000), 2);
        cart.printAll();
        cart.minus(new Product("사과", 1000), 3);
        cart.printAll();
    }
}

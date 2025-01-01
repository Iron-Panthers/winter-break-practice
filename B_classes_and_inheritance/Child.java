package B_classes_and_inheritance;

public class Child extends Person {
    @Override
    public void greet() {
        System.out.println("Hello, my name is {name} and I am an skibidi child");
    }

    @Override
    public void die() {
        System.out.println("died to a motorvehicle or firearms according to the us");
        kill(this);
    }
}

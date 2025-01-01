package B_classes_and_inheritance;

public class Adult extends Person {
    @Override
    public void greet() {
        System.out.println("Hello, my name is {name} and I am an adult");
    }

    @Override
    public void die() {
        System.out.println("ur old and now dead");
        kill(this);
    }
}

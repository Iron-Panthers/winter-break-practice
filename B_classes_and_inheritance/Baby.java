package B_classes_and_inheritance;

public class Baby extends Person {
    @Override
    public void greet() {
        System.out.println("Hello, my name is {name} and how many of these do we gotta do 😭😭😭");
    }

    @Override
    public void die() {
        System.out.println("infant mortality");
        kill(this);
    }
}

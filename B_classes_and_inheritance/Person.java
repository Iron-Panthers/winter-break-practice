package B_classes_and_inheritance;

public abstract class Person {
    private String name;
    private int age;
    private double height; // Stored in inches
    private double weight; // Stored in pounds
    private Jobs job;

    public Person() {
        name = "Bruce";
        age = 69;
        height = -5;
        weight = -1;
        job = Jobs.BRUCE;
    }

    public Person(String name, int age, double height, double weight, Jobs job) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.job = job;
    }

    public enum Jobs {
        DEV,
        BRUCE,
        BRYAN_SHIPPER/*💘*/
    }
    
    public double GetBMI() {
        return weight * Math.pow(height, 2) * 704;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Jobs getJob() {
        return job;
    }

    public void setJob(Jobs job) {
        this.job = job;
    }

    // THank you source actions 🙏🙏🙏🙏

    public String toString() {
        return "im not having fun with this one";
    }

    public void greet() {
        System.out.println("Hello, my name is {name}");
    }

    public void die() {
        kill(this); // suicide?
    }

    public static void kill(Person victim) {
        victim.age = 0;
        victim.height = 0;
        victim.weight = 0;
        victim.job = null;
        victim.name = null;

        System.gc(); // ik this technically doesnt do anything but it kinda funny
    }
}

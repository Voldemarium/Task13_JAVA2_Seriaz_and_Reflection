package utilites;

public class Person2 {
    private String name;
    private int age;

    public Person2(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "utilites.Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

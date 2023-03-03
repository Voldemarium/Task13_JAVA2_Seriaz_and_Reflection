package utilites;

public class Person  {//
    private String name;
    private Phone phone;
    private int age;

    public Person(String name, Phone phone, int age) {
        this.name = name;
        this.phone = phone;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", person2=" + phone +
                ", age=" + age +
                '}';
    }
}


package utilites;

public class Person {//implements Serializable
    private String name;
    private Person2 person2;
    private int age;

    public Person(String name, Person2 person2, int age) {
        this.name = name;
        this.person2 = person2;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", person2=" + person2 +
                ", age=" + age +
                '}';
    }
}


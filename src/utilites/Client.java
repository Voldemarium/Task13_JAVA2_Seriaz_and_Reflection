package utilites;

import java.io.Serializable;

public class Client  implements Serializable {//implements Serializable
    private int salary;
    private Person person;
    private int costProperty;


    public Client(int salary, Person person, int costProperty){
        this.salary = salary;
        this.person = person;
        this.costProperty = costProperty;

    }

    @Override
    public String toString() {
        return "utilites.Client{" +
                "salary=" + salary +
                ", person=" + person +
                ", costProperty=" + costProperty +
                '}';
    }
}

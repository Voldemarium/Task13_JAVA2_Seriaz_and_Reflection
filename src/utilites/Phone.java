package utilites;

public class Phone {
	private int number;
	private String model;

	public Phone(int number, String model) {
		this.number = number;
		this.model = model;
	}

	@Override
	public String toString() {
		return "Phone{" +
				"number=" + number +
				", model='" + model + '\'' +
				'}';
	}
}

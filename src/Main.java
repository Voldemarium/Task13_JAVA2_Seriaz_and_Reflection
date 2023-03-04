import utilites.Client;
import utilites.Person;
import utilites.Phone;

import java.io.*;
import java.lang.reflect.Field;

public class Main {
	public static void main(String[] args) {
		Phone phone1 = new Phone(12354, "Samsung");
		Person person = new Person("Vladimir", phone1, 45);
		Client client = new Client(20_000, person, 2_000_000);

		File myFile = new File("myFile.txt");   //создаем файл (пустой)

		//1.  Сериализация объекта
		ObjectOutputStream objOut = null;
		try {
			objOut = new ObjectOutputStream(new FileOutputStream(myFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		myWriteObject(phone1, objOut);
		myWriteObject(person, objOut);
		myWriteObject(client, objOut);

		//2.  Десериализация объекта
		ObjectInputStream objInput;
		try {
			objInput = new ObjectInputStream(new FileInputStream("myFile.txt"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Phone phoneNull = new Phone(0, null);
		Person personNull = new Person(null, phoneNull, 0);
		Client clientNull = new Client(0, personNull, 0);

		Phone phone2 = (Phone) myReadObject(phoneNull, objInput);
		System.out.println("phone2: " + phone2);

		Person person2 = (Person) myReadObject(personNull, objInput);
		System.out.println("person2: " + person2);

		Client client2 = (Client) myReadObject(clientNull, objInput);    //создадим новый обьект client2 (данные загрузим из файла)
		System.out.println("client2: " + client2);
	}

	//статический метод сериализации
	static public void myWriteObject(Object object, ObjectOutputStream objOut) {
		Class<?> clazz = object.getClass();
		//проверяем класс обьекта на сериализуемость
		boolean serializable = checkingForSerializable(clazz);
		if (serializable) {                    //Если обьект сериализуемый
			try {
				objOut.writeObject(object);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {                    //Если обьект несериализуемый
			//проверяем поля обьекта на сериализуемость
			Field[] fields = clazz.getDeclaredFields();
			//В случае, если поле не сериализуемо и не имеет своих полей, выбрасываем исключение
			if (fields.length < 1) {
				try {
					throw new NotSerializableException("Field is not serializable and not have fields!");
				} catch (NotSerializableException e) {
					throw new RuntimeException(e);
				}
			}
			for (Field field : fields) {
				field.setAccessible(true);
				Class<?> clazzField = field.getType();
				//проверяем класс полей обьекта на сериализуемость
				boolean serializableField = checkingForSerializable(clazzField);
				try {
					if (serializableField) { // если поле сериализуемо
						objOut.writeObject(field.get(object));            //записываем обьект-поле в файл
					} else {                // если поле несериализуемо
						myWriteObject(field.get(object), objOut);   // рекурсия
					}
				} catch (IllegalAccessException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	//статический метод десериализации
	static public Object myReadObject(Object object, ObjectInputStream objInput) {
		Class<?> clazz = object.getClass();
		boolean serializable = checkingForSerializable(clazz);
		if (serializable) {                    //Если обьект сериализуемый
			try {
				object = objInput.readObject();       //чтение данных из objInput (из файла "myFile.txt")
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		} else {
			Field[] fields = clazz.getDeclaredFields();
			//В случае, если поле не сериализуемо и не имеет своих полей, выбрасываем исключение
			if (fields.length < 1) {
				try {
					throw new NotSerializableException("Field is not serializable and not have fields!");
				} catch (NotSerializableException e) {
					throw new RuntimeException(e);
				}
			}
			for (Field field : fields) {
				field.setAccessible(true);
				Class<?> clazzField = field.getType();
				//проверяем класс полей обьекта на сериализуемость
				boolean serializableField = checkingForSerializable(clazzField);
				try {
					if (serializableField) {                       // если поле сериализуемо
						field.set(object, objInput.readObject());  //читаем из файла обьект-поле и перезаписываем поле объекта
					} else {                                       // если поле несериализуемо
						myReadObject(field.get(object), objInput); // рекурсия с объектом запрашиваемого типа
					}
				} catch (IllegalAccessException | IOException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return object;
	}

	//статический метод проверки класса на сериализуемость
	static public boolean checkingForSerializable(Class<?> clazz) {
		boolean serializable = clazz.isPrimitive();

		Class<?>[] ifs = clazz.getInterfaces();  //список интерфейсов
		for (
				Class<?> ifc : ifs) {
			if (ifc.getName().equals("java.io.Serializable")) {
				serializable = true;
				break;
			}
		}
		return serializable;
	}
}

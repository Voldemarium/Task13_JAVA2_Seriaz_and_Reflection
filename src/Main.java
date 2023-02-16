import utilites.Client;
import utilites.Person;
import utilites.Person2;

import java.io.*;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
//        Person person = new Person("Vova", 56);
        Person2 person2 = new Person2("Kolja", 56);
        Person person = new Person("Vladimir", person2, 45);

        Client client = new Client(20_000, person, 2_000_000);

        File myClient = new File("myClient.txt");   //создаем файл (пустой)
        ObjectOutputStream objOut = null;
        try {
            objOut = new ObjectOutputStream(new FileOutputStream(myClient));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myWriteObject(client, objOut);

        Client client2 = (Client) myReadObject("myClient.txt");    //создадим новый обьект client2 (данные загрузим из файла)
        System.out.println("client2: " + client2);

    }

    //статический метод сериализации
    static public void myWriteObject(Object object, ObjectOutputStream objOut) {
        Class<?> clazz = object.getClass();
        //проверяем класс обьекта на сериализуемость
        boolean serializable = checkingForSerializable(clazz);

        if (serializable && clazz.getName().equals("ObjectSerializableProxy")) {// Если обьект = ObjectSerializableProxy
            try {
                objOut.writeObject(object);           //запись обьекта (ручная сериализация из класса ObjectSerializableProxy
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (serializable) {                    //Если обьект сериализуемый, но не ObjectSerializableProxy
            Field[] fields = clazz.getDeclaredFields();
            boolean serializableField = false;

            for (Field field : fields) {
                Class<?> clazzField = field.getType();
                //проверяем класс полей обьекта на сериализуемость
                serializableField = checkingForSerializable(clazzField);
                if (!serializableField) {
                    break;
                }
            }
            if (!serializableField) {
                ObjectSerializableProxy objectSerializableProxy = new ObjectSerializableProxy(object);
                myWriteObject(objectSerializableProxy, objOut);    //РЕКУРСИЯ
            } else {
                try {
                    objOut.writeObject(object);   // запись обьекта (стандартная сериализация)
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Если обьект не имеет интерфейса Serializable, заворачиваем его в ObjectSerializableProxy
        } else {
            ObjectSerializableProxy objectSerializableProxy = new ObjectSerializableProxy(object);
            myWriteObject(objectSerializableProxy, objOut);    //РЕКУРСИЯ
        }
    }

    //статический метод десериализации из файла
    static public Object myReadObject(String file) {
        Object object = null;
        try (ObjectInputStream objInput = new ObjectInputStream(new FileInputStream(file))) {
            object = objInput.readObject();       //чтение данных из objInput (из файла file)
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        assert object != null;
        Class<?> clazz = object.getClass();
        if (clazz.getName().equals("ObjectSerializableProxy")) {  //Если класс обьекта - ObjectSerializableProxy
            object = ((ObjectSerializableProxy) object).getObject();
        }
        return object;
    }

    //статический метод проверки класса на сериализуемость
    static public boolean checkingForSerializable(Class<?> clazz) {
        //При clazz.getSuperclass() == null (в случае примитивов) это поле является сериализуемым
        boolean serializable = clazz.getSuperclass() == null;

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

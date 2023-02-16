import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ObjectSerializableProxy implements Serializable {
    private Object object;

    public ObjectSerializableProxy(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "ObjectSerializableProxy{" +
                "object =" + object +
                '}';
    }

    //Если в сериализуемом классе еть метод writeObject, то сериализация пойдет по этому методу
    private void writeObject(ObjectOutputStream out) throws IOException {
        //класс записываемого обьекта
        Class<?> clazz = object.getClass();
        out.writeObject(clazz);                     //Запишем первой строкой класс обьекта, для последующего извлечения класса при чтении
        //Запишем следующими строками поля обьектв
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //Проверим поле на сериализуемость
            boolean serializableField = Main.checkingForSerializable(field.getType());
            field.setAccessible(true);                       //делаем поле доступным
            if (serializableField) {
                try {
                    out.writeObject(field.get(object));      //запись сериализуемого поля обьекта object
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Class<?> clazzField = field.getClass();
                out.writeObject(clazzField);                  //Запишем след. класс несериализуемого поля, для последующего извлечения класса при чтении

                File fileField = new File(field.getType().getTypeName() + ".txt");   //создаем файл (пустой) с именем класса несериализуемого поля
                ObjectOutputStream objOut = null;
                try {
                    objOut = new ObjectOutputStream(new FileOutputStream(fileField));  //запись несериализуемого поля в файл
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Main.myWriteObject(field.get(object), objOut); //запись несериализуемого поля обьекта object
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //Прочитаем класс обьекта из первой записанной строки
        Class<?> clazz = (Class<?>) in.readObject();
        assert clazz != null;
        Field[] fields = clazz.getDeclaredFields();
        int countFields = fields.length;
        // выводим констукторы класса
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        for (Constructor<?> c : constructors) {
            int countParameters = c.getParameterCount();
            //находим кол-во параметров в конструкторе, если оно совпадает с количеством полей класса size
            if (countFields == c.getParameterCount()) {
                Object[] parameters = new Object[countParameters];
                for (int i = 0; i < countParameters; i++) {
                    //Проверим поле на сериализуемость
                    boolean serializableField = Main.checkingForSerializable(fields[i].getType());
                    if (serializableField) {
                        //                   parameters[i] = readField(in);
                        parameters[i] = in.readObject();
                    } else {
                        //Если поле несериализуемое - читаем класс этого поля
                        Class<?> clazzField = (Class<?>) in.readObject();
                        parameters[i] = Main.myReadObject(fields[i].getType().getTypeName() + ".txt");
                    }
                }
                this.object = c.newInstance(parameters);
            }
        }
    }
}

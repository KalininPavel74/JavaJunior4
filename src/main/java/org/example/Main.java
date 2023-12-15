package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Задание
 * =======
 * Создайте базу данных (например, SchoolDB).
 * В этой базе данных создайте таблицу Courses с полями id (ключ), title, и duration.
 * Настройте Hibernate для работы с вашей базой данных.
 * Создайте Java-класс Course, соответствующий таблице Courses, с необходимыми аннотациями Hibernate.
 * Используя Hibernate, напишите код для вставки, чтения, обновления и удаления данных в таблице Courses.
 * Убедитесь, что каждая операция выполняется в отдельной транзакции.
 */

public class Main {

    public static void main(String[] args) {

        checkConnect();

        /*
        // из лекции, чтобы это ни значило
        // в книжке также
        final StandardServiceRegistry registry
            = new StandardServiceRegistryBuilder()
                .configure()  // сам ищет файл hibernate.cfg.xml
                .build();
        SessionFactory sessionFactory
            = new MetadateSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();

         */

        try(SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Courses.class)
                .buildSessionFactory()){

            // Создание сессии
            Session session = sessionFactory.getCurrentSession();

            // Начало транзакции
            session.beginTransaction();

            // Создание объекта
            Courses course = new Courses("Курс биологии",21);
            System.out.println("Создан Текущий Курс: " + course);
            session.save(course);
            System.out.println("Текущий Курс сохранен в БД.");

            // Чтение объекта из базы данных
            course = session.get(Courses.class, course.getId());
            System.out.println("Текущий Курс извлечен из БД: " + course);

            // Обновление объекта
            course.setTitle("Курс химии");
            course.setDuration(18);
            System.out.println("Данные о Текущем Курсе обновлены: " + course);
            session.update(course);
            System.out.println("Данные о Текущий Курсе обновлены в БД");

            // Чтение объекта из базы данных
            course = session.get(Courses.class, course.getId());
            System.out.println("Текущий Курс извлечен из БД: " + course);

            session.delete(course);
            System.out.println("Текущий Курс удален из БД: "+course);

            // Чтение объекта из базы данных
            Courses course2 = session.get(Courses.class, course.getId());
            System.out.println("Текущий Курс извлечен из БД: " + course2);

            session.getTransaction().commit();

            System.out.println("Текущий Курс остался как объект Java: "+course);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void checkConnect() {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "root";

        // Подключение к базе данных
        try(Connection connection = DriverManager.getConnection(url, user, password)){

            String readDataSQL =  "SHOW DATABASES;";
            try (PreparedStatement statement = connection.prepareStatement(readDataSQL)) {
                ResultSet resultSet = statement.executeQuery();
                System.out.println("Перечень БД на СУБД сервере:");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(1));
                }
            }

            readDataSQL =  "select * from schooldb.courses";
            try (PreparedStatement statement = connection.prepareStatement(readDataSQL)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(1));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
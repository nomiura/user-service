package ru.test;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import ru.test.config.HibernateUtil;
import ru.test.dao.UserDao;
import ru.test.dao.UserDaoImpl;
import ru.test.service.UserService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        UserDao userDao = new UserDaoImpl(sessionFactory);
        UserService service = new UserService(userDao);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите номер операции:");
        while (true) {
            System.out.println("1. Создать пользователя");
            System.out.println("2. Список пользователей");
            System.out.println("3. Обновить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("0. Выйти");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Почта: ");
                    String email = scanner.nextLine();
                    System.out.print("Возраст: ");
                    int age;
                    while (true) {
                        try {
                            age = Integer.parseInt(scanner.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка: возраст должен быть числом. Попробуйте еще раз: ");
                        }
                    }
                    scanner.nextLine();
                    service.createUser(name, email, age);
                }
                case 2 -> service.getAllUsers().forEach(u ->
                        System.out.println(u.getId() + " | " + u.getName() + " | " + u.getEmail() + " | " + u.getAge()));
                case 3 -> {
                    System.out.print("Изменить ID пользователя: ");
                    long id = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Изменить имя: ");
                    String name = scanner.nextLine();
                    System.out.print("Изменить почту: ");
                    String email = scanner.nextLine();
                    System.out.print("Изменить возраст: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();
                    service.updateUser(id, name, email, age);
                }
                case 4 -> {
                    System.out.print("Введите ID пользователя для удаления: ");
                    long id = scanner.nextLong();
                    scanner.nextLine();
                    service.deleteUser(id);
                }
                case 0 -> System.exit(0);
            }
        }
    }
}
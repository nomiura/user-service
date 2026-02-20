package ru.test.dao;


import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.*;

import ru.test.config.HibernateUtil;
import ru.test.model.User;

import java.util.List;

@Testcontainers
public class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate-test.cfg.xml")
                .applySetting("hibernate.connection.url", postgres.getJdbcUrl())
                .applySetting("hibernate.connection.username", postgres.getUsername())
                .applySetting("hibernate.connection.password", postgres.getPassword())
                .build();

        Metadata metadata = new MetadataSources(registry)
                .buildMetadata();

        sessionFactory = metadata.buildSessionFactory();

        HibernateUtil.setSessionFactory(sessionFactory);

        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterEach
    void tearDown() {
        List<User> users = userDao.findAll();
        users.forEach(user -> {
            userDao.delete(user.getId());
        });
    }

    @Test
    @DisplayName("Пользователь должен сохраниться в БД")
    void saveUser() {
        User user = new User("Dmitry", "dimka2005@yandex.ru", 20);
        userDao.save(user);

        User found = userDao.findById(user.getId());

        assertNotNull(found);
        assertEquals("Dmitry", found.getName());
        assertEquals("dimka2005@yandex.ru", found.getEmail());
        assertEquals(20, (int) found.getAge());
    }

    @Test
    @DisplayName("Должен находить всех пользователей")
    void findAllUsers() {
        userDao.save(new User("Dmitry", "a@mail.ru", 20));
        userDao.save(new User("Alina", "b@mail.ru", 25));

        List<User> users = userDao.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Должен обновлять пользователя")
    void updateUser() {
        User user = new User("OldName", "old@mail.com", 30);
        userDao.save(user);

        user.setName("NewName");
        user.setAge(35);
        userDao.update(user);

        User updated = userDao.findById(user.getId());
        assertEquals("NewName", updated.getName());
        assertEquals(35, (int) updated.getAge());
    }

    @Test
    @DisplayName("Должен удалять пользователя")
    void deleteUser() {
        User user = new User("DeleteMe", "delete@mail.com", 20);
        userDao.save(user);

        userDao.delete(user.getId());
        User deleted = userDao.findById(user.getId());
        assertNull(deleted);
    }
}

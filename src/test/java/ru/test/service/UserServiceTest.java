package ru.test.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import ru.test.dao.UserDao;
import ru.test.model.User;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("Должен создать пользователя, если данные верны")
    void createUserWhenDataIsValid() {
        String name = "Dmitry";
        String email = "dimka2005@yandex.ru";
        int age = 20;

        userService.createUser(name, email, age);

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Пользователь не должен создаться, если введенная почта уже есть в БД")
    void createUserWhenEmailAlreadyExists() {
        when(userDao.findByEmail("dimka2005@yandex.ru"))
                .thenReturn(new User());

        userService.createUser("Dimochka", "dimka2005@yandex.ru", 20);
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Не должен создавать пользователя с некорректной почтой")
    void createUserWhenInvalidEmail() {
        userService.createUser("Dimochka", "sidhfwiu", 20);

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Не должен создавать пользователя с отрицательным возрастом")
    void createUserWhenNegativeAge() {
        userService.createUser("Liliya", "lilyaSTACY@max.ru", -52);

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Не должен создавать пользователя с именем с цифрами или с маленькой буквы")
    void createUserWhenInvalidName() {
        userService.createUser("nyanya", "nyaska@mail.ru", 36);
        userService.createUser("Nya6ka", "nananan@mail.ru", 40);

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Проверка корректности обновления пользователя")
    void updateUserShouldCallDaoUpdate() {
        Long id = 1L;
        User userFromDb = new User("Vasya", "old@gmail.com", 25);

        when(userDao.findById(id)).thenReturn(userFromDb);

        userService.updateUser(id, "Larisa", "larisskaIriska95@gmail.com", 25);

        verify(userDao, times(1)).update(userFromDb);

        assertEquals("Larisa", userFromDb.getName());
        assertEquals("larisskaIriska95@gmail.com", userFromDb.getEmail());
        assertEquals(25, (int) userFromDb.getAge());
    }

    @Test
    @DisplayName("Не обновляет пользователя, если почта уже существует в БД")
    void updateUserWhenEmailAlreadyExists() {
        Long id = 1L;

        User userFromDb = new User("Vasya", "old@mail.com", 30);
        userFromDb.setId(id);
        User anotherUser = new User("Someone", "existing@mail.com", 25);
        anotherUser.setId(2L);

        when(userDao.findById(id)).thenReturn(userFromDb);

        lenient().when(userDao.findByEmail("existing@mail.com")).thenReturn(anotherUser);

        userService.updateUser(id, "Vasya", "existing@mail.com", 30);

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Не обновляет пользователя, если данные некорректны")
    void updateUserWithInvalidData() {
        Long id = 1L;
        User userFromDb = new User("Oldname", "old@gmail.com", 30);

        when(userDao.findById(id)).thenReturn(userFromDb);

        userService.updateUser(id, "nyahaha", "newmylo@mylo.ru", 25);

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Не должен обновлять пользователя, если его нет в БД")
    void updateUserWhenUserNotExist() {
        Long id = 1L;

        when(userDao.findById(id)).thenReturn(null);

        userService.updateUser(id,"Michel", "michelsquadchanin@mail.ru", 45);

        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Удаление пользователя вызывает DAO")
    void deleteUserCallsDaoDelete() {
        Long id = 1L;

        userService.deleteUser(id);

        verify(userDao, times(1)).delete(id);
    }

    @Test
    @DisplayName("Поиск по пользователям должен возвращать список пользователей")
    void findAllReturnsAllUsers() {
        when(userDao.findAll()).thenReturn(
                List.of(
                        new User("Dmitry","dimkaCSgogogo@headshot.com", 13),
                        new User("Alina", "genshiLino4ka@givemegems.com", 19),
                        new User("Kiril", "kira@deathnote.letmewrite", 23)
                )
        );

        List<User> users = userService.getAllUsers();

        assertEquals(3, users.size());
    }
}

package ru.test.service;
import ru.test.dao.UserDao;
import ru.test.dao.UserDaoImpl;
import ru.test.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;
    private final Validator validator;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public void createUser(String name, String email, int age) {
        User user = new User(name, email, age);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            violations.forEach(v -> log.warn("Ошибка валидации: {}", v.getMessage()));
            return;
        }

        if (userDao.findByEmail(email) != null) {
            log.warn("Попытка создать пользователя с уже существующим email: {}", email);
            return;
        }

        try {
            userDao.save(user);
            log.info("Пользователь успешно создан: {}", email);
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя: {}", e.getMessage(), e);
        }
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(Long id, String name, String email, int age) {
        User user = userDao.findById(id);

        if (user == null) {
            log.warn("Попытка обновить несуществующего пользователя: {}", id);
            return;
        }

        if (!isValidName(name)) {
            log.warn("Ошибка валидации: Имя некорректно");
            return;
        }

        if (!isValidEmail(email)) {
            log.warn("Ошибка валидации: Email некорректен");
            return;
        }

        if (age < 0) {
            log.warn("Ошибка валидации: Возраст отрицательный");
            return;
        }

        User existingUser = userDao.findByEmail(email);
        if (existingUser != null && !existingUser.getId().equals(id)) {
            log.warn("Email уже существует: {}", email);
            return;
        }

        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        userDao.update(user);

        log.info("Пользователь обновлён: {}", id);
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    private boolean isValidName(String name) {
        return name != null
                && !name.isBlank()
                && name.length() >= 2
                && name.length() <= 50
                && name.matches("[A-Z][a-zA-Z]*");
    }

    private boolean isValidEmail(String email) {
        return email != null
                && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}

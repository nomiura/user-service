package service;
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
    private final UserDao userDao = new UserDaoImpl();
    private final Validator validator;

    public UserService() {
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
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            userDao.update(user);
        }
    }

    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}

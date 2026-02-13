package ru.test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Pattern(regexp = "^[А-ЯЁA-Z][а-яёa-zA-Z]*$", message = "Имя должно начинаться с заглавной буквы и содержать только буквы")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    @Column(unique = true, nullable = false)
    private String email;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 120, message = "Возраст слишком большой")
    private Integer age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
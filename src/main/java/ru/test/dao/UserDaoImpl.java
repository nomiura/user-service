package ru.test.dao;

import ru.test.config.HibernateUtil;
import ru.test.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public void save(User user) {
        executeInsideTransaction(session -> session.persist(user));
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    @Override
    public void update(User user) {
        executeInsideTransaction(session -> session.merge(user));
    }

    @Override
    public void delete(Long id) {
        executeInsideTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) session.remove(user);
        });
    }

    private void executeInsideTransaction(HibernateOperation operation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            operation.execute(session);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Ошибка в операции с базой данных: ", e);
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @FunctionalInterface
    private interface HibernateOperation {
        void execute(Session session);
    }
}
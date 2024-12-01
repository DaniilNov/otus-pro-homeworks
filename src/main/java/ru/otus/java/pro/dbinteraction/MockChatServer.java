package ru.otus.java.pro.dbinteraction;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MockChatServer {
    public static void main(String[] args) {
        DataSource dataSource = null;
        DbMigrator dbMigrator = null;
        try {
            System.out.println("Сервер чата запущен");
            dataSource = new DataSource("jdbc:h2:file:./db;MODE=PostgreSQL");
            dataSource.connect();

            dbMigrator = new DbMigrator(dataSource);
            dbMigrator.migrate();

            UsersDao usersDao = new UsersDao(dataSource);

            System.out.println(usersDao.getAllUsers());
            System.out.println("Все пользователи: " + usersDao.getAllUsers());

            AbstractRepository<User> usersRepository = new AbstractRepository<>(dataSource, User.class);
            usersRepository.save(new User(null, "Иван", "123", "Ivan"));
            System.out.println("Все пользователи: " + usersDao.getAllUsers());

            Optional<User> foundUser = usersRepository.findById(12L, User.class);
            foundUser.ifPresent(user -> System.out.println("Найден пользователь: " + user));

            if (foundUser.isPresent()) {
                User userToUpdate = foundUser.get();
                userToUpdate.setUserNickname("Ivanych");
                usersRepository.update(userToUpdate);
                System.out.println("Обновлен пользователь: " + userToUpdate);
            }

            List<User> users = usersRepository.findAll(User.class);
            System.out.println("Все пользователи: " + users);

            usersRepository.deleteById(1L);
            System.out.println("Пользователь с ID 1 удален");

            users = usersRepository.findAll(User.class);
            System.out.println("Все пользователи после удаления: " + users);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                dataSource.close();
            }
            System.out.println("Сервер чата завершил свою работу");
        }
    }
}

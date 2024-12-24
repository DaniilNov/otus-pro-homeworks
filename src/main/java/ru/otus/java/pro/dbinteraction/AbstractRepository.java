package ru.otus.java.pro.dbinteraction;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbstractRepository<T> {
    private final DataSource dataSource;
    private final String tableName;
    private final List<Field> cachedFields;
    private final Field idField;
    private final Class<T> cls;

    private PreparedStatement psInsert;

    public AbstractRepository(DataSource dataSource, Class<T> cls) {
        this.dataSource = dataSource;
        this.cls = cls;
        if (!cls.isAnnotationPresent(RepositoryTable.class)) {
            throw new ORMException("Класс " + cls.getName() + " не содержит аннотацию @RepositoryTable");
        }
        this.tableName = cls.getAnnotation(RepositoryTable.class).title();
        this.cachedFields = Arrays.stream(cls.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(RepositoryField.class))
                .collect(Collectors.toList());
        this.idField = Arrays.stream(cls.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(RepositoryIdField.class))
                .findFirst()
                .orElseThrow(() -> new ORMException("Не найдено поле с аннотацией @RepositoryIdField в классе " + cls.getName()));

        try {
            prepareStatements();
        } catch (SQLException e) {
            throw new ORMException("Не удалось инициализировать репозиторий для класса " + cls.getName(), e);
        }
    }

    public void save(T entity) {
        try {
            for (int i = 0; i < cachedFields.size(); i++) {
                cachedFields.get(i).setAccessible(true);
                psInsert.setObject(i + 1, cachedFields.get(i).get(entity));
            }
            psInsert.executeUpdate();
        } catch (Exception e) {
            throw new ORMException("Не удалось сохранить сущность: " + entity, e);
        }
    }

    public Optional<T> findById(Object id) {
        String query = String.format("SELECT * FROM %s WHERE %s = ?", tableName, getColumnName(idField));
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(query)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    T entity = cls.getDeclaredConstructor().newInstance();
                    for (Field field : cls.getDeclaredFields()) {
                        field.setAccessible(true);
                        field.set(entity, rs.getObject(getColumnName(field)));
                    }
                    return Optional.of(entity);
                }
            }
        } catch (Exception e) {
            throw new ORMException("Не удалось найти сущность с id: " + id, e);
        }
        return Optional.empty();
    }

    public List<T> findAll() {
        String query = "SELECT * FROM " + tableName;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                T entity = cls.getDeclaredConstructor().newInstance();
                for (Field field : cls.getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(entity, rs.getObject(getColumnName(field)));
                }
                result.add(entity);
            }
            return result;
        } catch (Exception e) {
            throw new ORMException("Не удалось получить все сущности из таблицы: " + tableName, e);
        }
    }

    public void update(T entity) {
        String query = "UPDATE " + tableName + " SET " +
                cachedFields.stream()
                        .map(f -> getColumnName(f) + " = ?")
                        .collect(Collectors.joining(", ")) +
                " WHERE " + getColumnName(idField) + " = ?;";
        try {
            PreparedStatement psUpdate = dataSource.getConnection().prepareStatement(query);
            for (int i = 0; i < cachedFields.size(); i++) {
                cachedFields.get(i).setAccessible(true);
                psUpdate.setObject(i + 1, cachedFields.get(i).get(entity));
            }
            idField.setAccessible(true);
            psUpdate.setObject(cachedFields.size() + 1, idField.get(entity));
            psUpdate.executeUpdate();
        } catch (Exception e) {
            throw new ORMException("Не удалось обновить сущность: " + entity, e);
        }
    }

    public void deleteById(Object id) {
        try {
            PreparedStatement psDelete = dataSource.getConnection().prepareStatement("DELETE FROM " + tableName + " WHERE " + getColumnName(idField) + " = ?;");
            psDelete.setObject(1, id);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            throw new ORMException("Не удалось удалить сущность с id: " + id, e);
        }
    }

    private void prepareStatements() throws SQLException {
        prepareInsertStatement();
    }

    private void prepareInsertStatement() throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName).append(" (");
        cachedFields.forEach(f -> query.append(getColumnName(f)).append(", "));
        query.setLength(query.length() - 2);
        query.append(") VALUES (");
        cachedFields.forEach(f -> query.append("?, "));
        query.setLength(query.length() - 2);
        query.append(");");

        psInsert = dataSource.getConnection().prepareStatement(query.toString());
    }

    private String getColumnName(Field field) {
        RepositoryField annotation = field.getAnnotation(RepositoryField.class);

        return (annotation != null && !annotation.columnName().isEmpty()) ? annotation.columnName() : field.getName();
    }
}

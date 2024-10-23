package ru.otus.java.pro.streamapi;

import ru.otus.java.pro.streamapi.model.Status;
import ru.otus.java.pro.streamapi.model.Task;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Task> tasks = Arrays.asList(
                new Task(1, "Task 1", Status.OPEN),
                new Task(2, "Task 2", Status.IN_PROGRESS),
                new Task(3, "Task 3", Status.CLOSED),
                new Task(4, "Task 4", Status.OPEN),
                new Task(5, "Task 5", Status.IN_PROGRESS)
        );

        List<Task> openTasks = getTasksByStatus(tasks, Status.OPEN);
        System.out.println("Open Tasks: " + openTasks);

        boolean hasTaskWithId = hasTaskWithId(tasks, 3);
        System.out.println("Has task with ID 3: " + hasTaskWithId);

        List<Task> sortedTasks = getTasksSortedByStatus(tasks);
        System.out.println("Sorted Tasks: " + sortedTasks);

        long countOpenTasks = countTasksByStatus(tasks, Status.OPEN);
        System.out.println("Count of Open Tasks: " + countOpenTasks);
    }

    /**
     * Получает список задач по выбранному статусу
     *
     * @param tasks  список задач
     * @param status статус для фильтрации задач
     * @return список задач с указанным статусом
     */
    public static List<Task> getTasksByStatus(List<Task> tasks, Status status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет наличие задачи с указанным ID
     *
     * @param tasks список задач
     * @param id    ID задачи для проверки
     * @return true, если задача с указанным ID существует, иначе false
     */
    public static boolean hasTaskWithId(List<Task> tasks, int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findAny()
                .isPresent();
    }

    /**
     * Получает список задач, отсортированных по статусу
     * Порядок сортировки: OPEN, IN_PROGRESS, CLOSED
     *
     * @param tasks список задач
     * @return отсортированный список задач
     */
    public static List<Task> getTasksSortedByStatus(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getStatus, Comparator.comparingInt(Status::ordinal)))
                .collect(Collectors.toList());
    }

    /**
     * Подсчитывает количество задач по определенному статусу
     *
     * @param tasks  список задач
     * @param status статус для фильтрации задач
     * @return количество задач с указанным статусом
     */
    public static long countTasksByStatus(List<Task> tasks, Status status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }
}

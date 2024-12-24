package ru.otus.java.pro.dbinteraction;

@RepositoryTable(title = "users")
public class User {
    @RepositoryIdField
    private Long id;
    @RepositoryField
    private String login;
    @RepositoryField(columnName = "password")
    private String userPassword;
    @RepositoryField(columnName = "nickname")
    private String userNickname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public User() {
    }

    public User(Long id, String login, String userPassword, String userNickname) {
        this.id = id;
        this.login = login;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + userPassword + '\'' +
                ", nickname='" + userNickname + '\'' +
                '}';
    }
}

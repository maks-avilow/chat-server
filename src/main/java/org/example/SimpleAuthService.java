package org.example;

import java.sql.*;

public class SimpleAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;

    private static final String INSERT_NEW_USER = "INSERT INTO users(login, password, nick) VALUES(?, ?, ?);";
    private static final String LOGIN = "SELECT nick FROM users WHERE login = ? AND password = ?;";
    private static final String URL = "jdbc:sqlite:chat.db";


    public static void insertNewUsers(final String login, final String password, final String nick) throws SQLException {
        connectBase();
        try (final PreparedStatement ps = connection.prepareStatement(INSERT_NEW_USER)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, nick);

        }
    }
    @Override
    public String getNicknameByLoginAndPassword(final String login, final String password) throws SQLException {
        if (connection != null) {
            try (final PreparedStatement ps = connection.prepareStatement(LOGIN)) {
                ps.setString(1, login);
                ps.setString(2, password);

            }
        }
        return null;
    }

    public static void connectBase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(URL);
        statement = connection.createStatement();
    }

    public static void disconnectBase() {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}




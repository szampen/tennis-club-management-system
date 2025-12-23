package com.tennis;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tennis_club", "root", "Konfucjonizm1");

    }
}

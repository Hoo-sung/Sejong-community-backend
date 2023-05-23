package sejong.back.domain;

import java.sql.DriverManager;

public abstract class ConnectionConst {

//    public static final String URL= "jdbc:mysql://localhost:3306/Sejong_Community";
//    public static final String USERNAME="root";
//    public static final String PASSWORD="eodnjs1405-";

    public static final String URL = "jdbc:h2:tcp://localhost/~/test;MODE=MySql;";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";


}

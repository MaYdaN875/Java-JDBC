package com.maydan.proyecto.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static HikariDataSource dataSource;

    static {
        System.out.println("Bloque static...");

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://ep-shy-meadow-ar98e1fm-pooler.c-4.us-west-2.aws.neon.tech/neondb?sslmode=require");
        config.setUsername("neondb_owner");
        config.setPassword("npg_3baZtL9fosJn");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        config.setLeakDetectionThreshold(15000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool(){
        if(dataSource!=null && !dataSource.isClosed()){
            dataSource.close();
            System.out.println("Connection Pool cerrado...");
        }
    }

}

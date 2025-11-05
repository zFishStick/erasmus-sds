// package com.sds2.database;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.util.logging.Logger;

// import org.springframework.stereotype.Component;

// @Component
// public class Database {

//     private Logger logger = Logger.getLogger(Database.class.getName());
//     private Connection c = null;

//     public Database() {
//         try {
//             c = DriverManager.getConnection(
//                     "jdbc:postgresql://localhost:5432/TravelPlannerDB",
//                     "postgres",
//                     System.getenv("POSTGRE_PSW")
//             );
//             logger.info("Opened database successfully");
//         } catch (Exception e) {
//             logger.severe(e.getClass().getName() + ": " + e.getMessage());
//             System.exit(0);
//         }
//     }

//     public Connection getConnection() {
//         return c;
//     }
// }

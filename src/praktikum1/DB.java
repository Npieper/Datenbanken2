/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package praktikum1;

/**
 *
 * @author NiklasPieper
 */
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.pool.OracleDataSource;


public class DB {
    public static Connection connect() throws SQLException {

        String treiber;
        OracleDataSource ods = new OracleDataSource();
        treiber = "oracle.jdbc.driver.OracleDriver";
        Connection dbConnection = null;

        try {
            Class.forName(treiber).newInstance();
            System.out.println("Treiber geladen !");
        } catch (Exception e) {
            System.out.println("fehler beim laden des Treibers: " + e.getMessage());
        }
        ods.setURL("jdbc:oracle:thin:dbprak18/dbprak18@Schelling.nt.fh-koeln.de:1521:xe");
        dbConnection = ods.getConnection();
        System.out.println(dbConnection.getClientInfo());
        return dbConnection;
    } 
    
    
}

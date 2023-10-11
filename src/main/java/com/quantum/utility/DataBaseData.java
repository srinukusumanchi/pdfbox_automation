/**
 * User: Srinu Kusumanchi s3810121
 * Created Date: 2022-02-07
 * Created Time: 11:02 a.m.
 */

package com.quantum.utility;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataBaseData {
    private final JdbcTemplate jdbc;

    public DataBaseData(List<Map<String, String>> dbDetails) throws IOException {
        String dbHost = dbDetails.stream().map(x -> x.get("dbHost")).collect(Collectors.toList()).toString().replaceAll("\\[|\\]", "");
        String dbPort = dbDetails.stream().map(x -> x.get("dbPort")).collect(Collectors.toList()).toString().replaceAll("\\[|\\]", "");
        String databaseName = dbDetails.stream().map(x -> x.get("databaseName")).collect(Collectors.toList()).toString().replaceAll("\\[|\\]", "");
        String dbUserName = dbDetails.stream().map(x -> x.get("dbUserName")).collect(Collectors.toList()).toString().replaceAll("\\[|\\]", "");
        String dbPassword = dbDetails.stream().map(x -> x.get("dbPassword")).collect(Collectors.toList()).toString().replaceAll("\\[|\\]", "");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        dataSource.setUrl("jdbc:db2://" + dbHost + ":" + dbPort + "/" + databaseName);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        jdbc = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> getResultsFromDB(final String query) {
        List<Map<String, Object>> resultSetData = null;
        try {
            resultSetData = jdbc.queryForList(query);
//            System.out.println("Query:-" + query);
        } catch (Exception e) {
            System.err.println("Query is not given properly, Error is:-" + e.getMessage()
                    + "Query is :-" + query);
            /*Assert.fail("Query is not given properly, Error is:-" + e.getMessage()
                    + "Query is :-" + query);*/
        }
        return resultSetData;
    }

    public List<Map<String, Object>> getResultsListsFromDB(final String query) {
        List<Map<String, Object>> resultSetData = null;
        try {
            resultSetData = jdbc.queryForList(query);
//            System.out.println("Query:-" + query);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assert.fail("Query is not given properly, Error is:-" + e.getMessage()
                    + "Query is :-" + query);
        }
        return resultSetData;
    }
}

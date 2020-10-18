package app;

import java.sql.*;

public class DAO {
    private final static String URL = "jdbc:mysql://localhost:3306/office?serverTimezone=UTC";
    private final static String USER = "admin";
    private final static String PASSWORD = "admin";
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final Connection con;
    //Queries
    private final static String NEW_REQUEST_QUERY = "INSERT INTO request_type (tag_name,avg_time) VALUES (?,?)";
    //Get how many customers have been served for each request type
    private final static String GET_STATS_BASE =
            "SELECT COUNT(*) AS req_count, RT.tag_name " +
                    "FROM request R, request_type RT " +
                    "WHERE R.ref_type = RT.id " +
                    "GROUP BY RT.id,RT.tag_name";
    //Get  the number of customers each counter has served divided by request type
    private final static String GET_STATS_PER_COUNTER =
            "SELECT COUNT(*) AS req_count, RT.tag_name, R.counter " +
                    "FROM request R, request_type RT " +
                    "WHERE R.ref_type = RT.id " +
                    "GROUP BY R.counter,RT.tag_name";

    //Constructor
    public DAO() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER); //Loading driver
        con = DriverManager.getConnection(URL, USER, PASSWORD); //Getting connection
    }
    //Closing connection
    public void close() throws SQLException {
        con.close(); //Closing Connection
    }

    //DB query functions
    public void createRequestType(String tagName, Float avgTime) throws SQLException {
        PreparedStatement pst = con.prepareStatement(NEW_REQUEST_QUERY);
        //Setting parameters
        pst.setString(1, tagName);
        pst.setFloat(2, avgTime);
        pst.execute(); //Executing query
    }
    public ResultSet getAllStats(Character DayWeekMonth) throws SQLException {
        Statement st = con.createStatement();
        return switch (DayWeekMonth) {
            case 'w' -> st.executeQuery(GET_STATS_BASE + " WEEK(R.date)");
            case 'm' -> st.executeQuery(GET_STATS_BASE + " MONTH(R.date)");
            default -> st.executeQuery(GET_STATS_BASE);
        };
    }
    public ResultSet getAllStatsByCounter(Character DayWeekMonth) throws SQLException {
        Statement st = con.createStatement();
        return switch (DayWeekMonth) {
            case 'w' -> st.executeQuery(GET_STATS_PER_COUNTER + " WEEK(R.date)");
            case 'm' -> st.executeQuery(GET_STATS_PER_COUNTER + " MONTH(R.date)");
            default -> st.executeQuery(GET_STATS_PER_COUNTER);
        };
    }
}

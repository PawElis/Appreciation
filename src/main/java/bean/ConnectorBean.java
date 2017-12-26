package bean;

import org.apache.log4j.Logger;
import structures.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 */

public class ConnectorBean {
    private static Statement stmt;
    private boolean initialized = false;
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    public void init() {
        if (initialized) {
            return;
        }
        log.info("Bean initialization start.");
        DataSource ds;
        try {
            InitialContext ictx = new InitialContext();
            Context context = (Context) ictx.lookup("java:comp/env");
            ds = (DataSource) context.lookup("jdbc/appreciation");
        } catch (NamingException e) {
            log.warn("Failed to locate datasource.", e);
            return;
        }
        try {
            Locale.setDefault(Locale.ENGLISH);
            Connection con = ds.getConnection();
            stmt = con.createStatement();
        } catch (SQLException e) {
            log.warn("Failed to establish connection.", e);
            return;
        }
        initialized = true;
    }

    public void destroy() {
        log.debug("Bean deinitialization start.");
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.warn("Failed to close connection!", e);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    private ResultSet query(String query) {
        log.trace("SQL query: " + query);
        ResultSet rs;
        if (!initialized) {
            log.fatal("");
            return null;
        }
        try {
            rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            log.warn("SQL failure during query execution.", e);
            return null;
        }
    }

    // Table Vocabulary
    private ArrayList<String> ResultSetToVocabularyWords(ResultSet rs) {
        log.info("ResultSet to AccountRecord list conversion.");
        ArrayList<String> result = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    result.add(rs.getString("WORD"));
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
                return new ArrayList<>();
            }
        }
        return result;
    }

    public ArrayList<String> getVocabulary(int id) {
        ResultSet rs = query("SELECT * FROM USER1.vocabulary WHERE APPRECIATION_TYPE_ID = '" + id + "'");
        return ResultSetToVocabularyWords(rs);
    }

    // Table Places
    private ArrayList<Places> ResultSetToPlaces(ResultSet rs) {
        log.info("ResultSet to PlacesRecord list conversion.");
        ArrayList<Places> result = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    result.add(new Places(
                            rs.getInt("ID_PLACE"),
                            rs.getFloat("LONGITUDE"),
                            rs.getFloat("LATITUDE"),
                            rs.getString("DATE_UPDATE"),
                            rs.getInt("ID_USER")
                    ));
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
                return new ArrayList<>();
            }
        }
        return result;
    }

    //todo переделать запрос
    public int sizePlaces(){
        return getPlaces().size();
    }

    public ArrayList<Places> getPlaces() {
        ResultSet rs = query("SELECT * FROM USER1.PLACES");
        return ResultSetToPlaces(rs);
    }

    //todo переделать
    public ArrayList<Places> getPlacesToAdvised(ArrayList<Integer> arrayList) {
        ResultSet rs = query("SELECT * FROM USER1.PLACES");
        return ResultSetToPlaces(rs);
    }

    public ArrayList<Places> getPlaces(String date) {
        ResultSet rs = query("SELECT * FROM USER1.PLACES WHERE DATE_UPDATE <= to_date('" + date + "','dd-MM-yyyy')");
        return ResultSetToPlaces(rs);
    }

    public void updatePlaces(ArrayList<Places> data) throws SQLException {
        for (Places x : data) {
            String queryString = "INSERT INTO USER1.places (ID_PLACE, LONGITUDE, LATITUDE, DATE_UPDATE, ID_USER) VALUES('" + x.getIdPlace() + "','" + Double.toString(x.getLongitude()) + "','"
                    + Double.toString(x.getLatitude()) + "',to_date('" + x.getDataUpdate() + "','dd-MM-yyyy'),'" + x.getIdUser() + "')";
            try {
                stmt.executeUpdate(queryString);
            } catch (SQLException e) {
                log.warn("SQL failure during PlacesRecord-based update.", e);
            }
        }
    }

    public Places getIdPlaceByLongitude(float longitude) {
        ResultSet rs = query("SELECT * FROM USER1.PLACES WHERE longitude = '" + longitude + "'");
        if (ResultSetToPlaces(rs).size() != 0) {
            return ResultSetToPlaces(rs).get(0);
        }
        return new Places(1, 1, 1, "1", 1);
    }

    // Table Type_Of_Appreciation
    private ArrayList<TypeOfAppreciation> ResultSetToType_Of_Appreciation(ResultSet rs) {
        log.info("ResultSet to Type_Of_Appreciation list conversion.");
        ArrayList<TypeOfAppreciation> result = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    result.add(new TypeOfAppreciation(
                            rs.getInt("APPRECIATION_TYPE_ID"),
                            rs.getString("NAME_VOCABULARY"),
                            rs.getString("NAME_VOCABULARY_ENG"),
                            rs.getString("COLOR")
                    ));
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
                return new ArrayList<>();
            }
        }
        return result;
    }

    public ArrayList<TypeOfAppreciation> getAllTypeOfAppreciation() {
        ResultSet rs = query("SELECT * FROM USER1.TYPE_OF_APPRECIATION");
        return ResultSetToType_Of_Appreciation(rs);
    }

    //todo переделать запрос
    public int numOfVocabulary() {
        return getAllTypeOfAppreciation().size();
    }

    // Table Users
    private ArrayList<Users> ResultSetToUsers(ResultSet rs) {
        log.info("ResultSet to UserRecord list conversion.");
        ArrayList<Users> result = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    result.add(new Users(
                            rs.getInt("ID_USER"),
                            rs.getString("NAME"),
                            rs.getString("PHOTO")
                    ));
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
                return new ArrayList<>();
            }
        }
        return result;
    }

    public ArrayList<Users> getUsers() {
        ResultSet rs = query("SELECT * FROM USER1.USERS");
        return ResultSetToUsers(rs);
    }

    public void updateUsers(ArrayList<Users> data) throws SQLException {
        for (Users x : data) {
            String queryString = "INSERT INTO USER1.USERS (ID_USER, NAME, PHOTO) VALUES('" + x.getIdUser() + "','"
                    + x.getName() + "','"
                    + x.getPhoto() + "')";
            try {
                stmt.executeUpdate(queryString);
            } catch (SQLException e) {
                log.warn("SQL failure during Users-based update.", e);
            }
        }
    }

    // Table Appreciation
    private ArrayList<Appreciation> ResultSetToAppreciation(ResultSet rs) {
        log.info("ResultSet to AppreciationRecord list conversion.");
        ArrayList<Appreciation> result = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    result.add(new Appreciation(
                            rs.getInt("ID_APPRECIATION"),
                            rs.getInt("ID_PLACE"),
                            rs.getInt("APPRECIATION_TYPE_ID"),
                            rs.getByte("WEIGHT"),
                            rs.getInt("NUMBER_OF_APPRECIATIONS"),
                            rs.getInt("ID_USER")
                    ));
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
                return new ArrayList<>();
            }
        }
        return result;
    }

    public ArrayList<Appreciation> getAllAppreciation() {
        ResultSet rs = query("SELECT * FROM USER1.APPRECIATION");
        return ResultSetToAppreciation(rs);
    }

    public ArrayList<Appreciation> getAllAppreciationToUser(int idUser) {
        ResultSet rs = query("SELECT * FROM USER1.APPRECIATION WHERE ID_USER = '" + idUser + "'");
        return ResultSetToAppreciation(rs);
    }

    public void updateAppreciation(ArrayList<Appreciation> data) throws SQLException {
        for (Appreciation x : data) {
            String queryString;
            if (x.getIdUser() != -1) {
                queryString = "INSERT INTO USER1.APPRECIATION (ID_PLACE, APPRECIATION_TYPE_ID," +
                        " WEIGHT, NUMBER_OF_APPRECIATIONS, ID_USER) VALUES('" + Integer.toString(x.getIdPlace()) + "','"
                        + Integer.toString(x.getAppreciationTypeId()) + "','" + Integer.toString(x.getWeight()) + "','"
                        + Integer.toString(x.getNumberOfAppreciation()) + "','" + Integer.toString(x.getIdUser()) + "')";
            } else {
                queryString = "INSERT INTO USER1.APPRECIATION (ID_PLACE, APPRECIATION_TYPE_ID," +
                        " WEIGHT, NUMBER_OF_APPRECIATIONS) VALUES('" + Integer.toString(x.getIdPlace()) + "','"
                        + Integer.toString(x.getAppreciationTypeId()) + "','" + Integer.toString(x.getWeight()) + "','"
                        + Integer.toString(x.getNumberOfAppreciation()) + "')";
            }
            try {
                stmt.executeUpdate(queryString);
            } catch (SQLException e) {
                log.warn("SQL failure during Appreciation-based update.", e);
            }
        }
    }

    public Integer getIdAppreciationByIdPlace(int idPlace) {
        ResultSet rs = query("SELECT * FROM APPRECIATION WHERE ID_PLACE = \"" + idPlace + "\"");
        Integer result = null;
        if (rs != null) {
            try {
                rs.next();
                result = rs.getInt("Id_Appreciation");
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during idPlace ID retrieval.");
            }
        }
        return result;
    }

    // Table Users_For_Information
    public int getIdUserForInformation() {
        ResultSet rs = query("SELECT * FROM (SELECT * FROM USER1.USERS_FOR_INFORMATION) WHERE rownum = 1");
        return ResultIdUser(rs);
    }

    private int ResultIdUser(ResultSet rs) {
        log.info("ResultIdUser to Users_For_Information Record list conversion.");
        int result = 1;
        if (rs != null) {
            try {
                while (rs.next()) {
                    result = rs.getInt("ID_USER");
                }
                rs.close();
            } catch (SQLException e) {
                log.warn("SQL failure during ResultSet parsing.", e);
            }
        }
        return result;
    }

    public static void deleteIdUserForInformation(int id) {
        String queryString;
        queryString = "DELETE FROM USER1.USERS_FOR_INFORMATION WHERE ID_USER  =" + id;
        try {
            stmt.executeUpdate(queryString);
        } catch (SQLException e) {
            log.warn("SQL failure during Users_For_Information-based update.", e);
        }
    }

    public static void addFriendsToInformation(ArrayList<Integer> data) throws SQLException {
        for (int x : data) {
            String queryString;
                queryString = "INSERT INTO USER1.USERS_FOR_INFORMATION (ID_USER) VALUES('"+ x +"')";
            try {
                stmt.executeUpdate(queryString);
            } catch (SQLException e) {
                log.warn("SQL failure during Users_For_Information-based update.", e);
            }
        }
    }
}
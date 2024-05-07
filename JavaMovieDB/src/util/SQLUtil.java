package util;

import entity.Person;
import exce.NullDataTypeException;

import java.math.BigInteger;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class SQLUtil {
    public enum DataType{
        INT, BIGINT, DECIMAL, VARCHAR, TEXT, DATE
    }
    public static class DataInfo{
        public String attri_name;
        public DataType attri_type;
        public BigInteger int_value;
        public Double double_value;
        public String str_value;
        public Date date_value;

        public DataInfo() {}
        public DataInfo(String attri_name, DataType attri_type, BigInteger value) {
            this.attri_name = attri_name;
            this.attri_type = attri_type;
            this.int_value = value;
        }
        public DataInfo(String attri_name, DataType attri_type, int value) {
            this.attri_name = attri_name;
            this.attri_type = attri_type;
            this.int_value = BigInteger.valueOf(value);
        }
        public DataInfo(String attri_name, DataType attri_type, Double value) {
            this.attri_name = attri_name;
            this.attri_type = attri_type;
            this.double_value = value;
        }
        public DataInfo(String attri_name, DataType attri_type, String value) {
            this.attri_name = attri_name;
            this.attri_type = attri_type;
            this.str_value = value;
        }
        public DataInfo(String attri_name, DataType attri_type, Date value) {
            this.attri_name = attri_name;
            this.attri_type = attri_type;
            this.date_value = value;
        }
    }

    public static String DataParser(DataInfo di) {
        String data = null;
        try {
            switch (di.attri_type) {
                case INT, BIGINT -> {
                    data = di.int_value.toString();
                }
                case DECIMAL -> {
                    data = Double.toString(di.double_value);
                }
                case VARCHAR, TEXT -> {
                    data = '\'' + di.str_value + '\'';
                }
                case DATE -> {
                    data = '\'' + new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(di.date_value) + '\'';
                }
                default -> {
                    throw new NullDataTypeException();
                }
            }
        } catch (NullDataTypeException ndte) {
            ndte.printStackTrace();
        }
        return data;
    }

    public static int Update(String sql) {
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            pstmt = conn.prepareStatement(sql);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        return count;
    }

    public static List<String> CountAndResolve(String sql) {
        Connection conn = DBConnector.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<String> count = new ArrayList<>();

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                count.add(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBConnector.closeConnection(conn, pstmt, rs);
        }

        if (count.isEmpty())
            count.add("0");

        return count;
    }
}

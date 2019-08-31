package com.blanke.wechatbotxposed.Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Date;

import de.robv.android.xposed.XposedBridge;


public class JavaMySql {
    private int dataKey = 0;
    private int dataMessage = 0;
    private int counter = 0;

    public void setCounter(int c) {
        counter = c;
    }
    public int getCounter() {
        return counter;
    }

    private Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://106.14.184.10:3306/wxrobot?useUnicode=" +
                            "yes&characterEncoding=UTF-8",
                    "wxrobot","hwi1VWA5R8VPdbKx");
            XposedBridge.log("Connected to" + con);
            return con;
        }
        catch (Exception e) {
            XposedBridge.log("Can't Connect to Database");
            XposedBridge.log(e);
            return null;
        }
    }

    public Data[] getData(String key) {
        Connection con = this.getConnection();
        Data[] noResult = {};
        Data[] recent = new Data[5];
        if (con == null) {
            return noResult;
        }

        try {
            Statement st = con.createStatement();
            String sql = ("SELECT * FROM wx_message ORDER BY addTime DESC;");
            ResultSet rs = st.executeQuery(sql);


            while (rs.next() && counter < 5) {
                int id = rs.getInt("id");
                String content = rs.getString("message");
                String keyTerm = rs.getString("pregKeyName");
                int time = rs.getInt("addTime");
                java.util.Date realTime= new java.util.Date((long)time*1000);

                if (keyTerm.compareTo(key) == 0 ) {
                    Data d = new Data(content, realTime, keyTerm);
                    recent[counter] = d;
                    counter++;
                }
            }

            con.close();
            return recent;
        }
        catch (Exception e) {
            XposedBridge.log(e);
            return noResult;
        }
    }


}

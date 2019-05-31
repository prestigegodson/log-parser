package com.ef.services;

import com.ef.enums.LogDuration;
import com.ef.models.Entries;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Scanner;

import static java.time.Instant.ofEpochMilli;

/**
 * Created by prest on 5/30/2019.
 */
public class LogService {

    public void loadLogFile(String filePath){

        //clear table;
        this.deleteAllRecords();

        File file = new File(filePath);

        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            Scanner scanner = new Scanner(inputStream);

            while(scanner.hasNext()){
                String record = scanner.nextLine();
                this.saveRecord(this.convertToEntity(record));
            }

            scanner.close();
            inputStream.close();
            System.out.println("Completed Loading Log File... ");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRecord(Entries entries){

        String sql = String.format("insert into %s (entry_date, ip_address, http_method, response_code, user_agent) " +
                "values(?,?,?,?,?)", DBConnection.ENTRIES_TBL);
        try {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setTimestamp(1,entries.getDate());
            statement.setString(2,entries.getIpAddress());
            statement.setString(3,entries.getHttpMethod());
            statement.setInt(4,entries.getResponseCode());
            statement.setString(5,entries.getUserAgent());

           statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllRecords(){

        String sql = String.format("Delete from %s ", DBConnection.ENTRIES_TBL);
        try {

            Statement statement = DBConnection.getConnection().createStatement();

            statement.execute(sql);
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllBlockedIpRecords(){

        String sql = String.format("Delete from %s ", DBConnection.BLOCKED_IP_TBL);
        try {

            Statement statement = DBConnection.getConnection().createStatement();

            statement.execute(sql);
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Entries convertToEntity(String record){

        String[] fields = record.split("\\|");

        Entries entries = new Entries();

        Timestamp date = Timestamp.valueOf(fields[0]);
        entries.setDate(date);
        entries.setIpAddress(fields[1]);
        entries.setHttpMethod(fields[2].replace("\"",""));
        entries.setResponseCode(Integer.parseInt(fields[3]));
        entries.setUserAgent(fields[4].replace("\"",""));

        return entries;
    }

    public void queryLogFile(Timestamp startDate, LogDuration duration, int threshold){

        //Clear blocked ip table records
        this.deleteAllBlockedIpRecords();

        LocalDateTime localDateTime = ofEpochMilli(startDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endLocalDateTime = null;

        if(duration.toString().equalsIgnoreCase(LogDuration.daily.toString())){
            endLocalDateTime = localDateTime.plusHours(24);
        }else if(duration.toString().equalsIgnoreCase(LogDuration.hourly.toString())){
            endLocalDateTime = localDateTime.plusHours(1);
        }


        Timestamp endDate = Timestamp.valueOf(endLocalDateTime);

        String sql = String.format("Select ip_address, count(*) from %s " +
                "where entry_date between ? AND ? " +
                "group by ip_address having count(*) > ?", DBConnection.ENTRIES_TBL);



        try {

            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setTimestamp(1, startDate);
            statement.setTimestamp(2, endDate);
            statement.setInt(3, threshold);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                int hits = rs.getInt(2);
                String ipAddress = rs.getString(1);
                String reason = String.format("Made %d request in %d hour(s)", hits,
                        duration.toString().equalsIgnoreCase("daily") ? 24 : 1);

                System.out.println(String.format("IP : %s, Hit: %d",ipAddress,hits));
                this.saveBockedIpAddress(ipAddress, reason);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveBockedIpAddress(String ipAddress, String reason){

        String sql = String.format("insert into %s (ip_address, reason) values(?,?)", DBConnection.BLOCKED_IP_TBL);

        try {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(sql);
            statement.setString(1, ipAddress);
            statement.setString(2,reason);

            statement.execute();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

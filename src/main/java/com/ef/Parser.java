package com.ef;

import com.ef.dto.MyOptions;
import com.ef.enums.LogDuration;
import com.ef.services.LogService;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.sql.Timestamp;

/**
 * Created by prest on 5/31/2019.
 */
public class Parser {

    public static void main(String[] args) {

        try {
            LogService logService = new LogService();

            MyOptions myOptions = new MyOptions();
            CmdLineParser parser = new CmdLineParser(myOptions);

            parser.parseArgument(args);
            logService.loadLogFile(myOptions.getAccessLog());

            if(myOptions.getStartDate() == null){
                System.err.println("Start date is required");
                return;
            }

            int threshold = myOptions.getThreshold();
            Timestamp startDate = Timestamp.valueOf(myOptions.getStartDate().replace("."," "));
            LogDuration duration = LogDuration.valueOf(myOptions.getDuration());

            if(threshold < 1){
                System.err.println("Threshold should be greater than 0");
                return;
            }else if(startDate == null){
                System.err.println("Start date is not a valid date type");
                return;
            }else if(duration == null){
                System.err.println("Duration can either be hourly or daily");
                return;
            }

            logService.queryLogFile(startDate, duration, threshold);



        } catch (CmdLineException e) {
            e.printStackTrace();
        }


    }
}

package log;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Paul Lancaster on 31/10/2016
 */
public class Logger {
    private BufferedOutputStream out;
    private Calendar calendar;
    
    public Logger(File logFile) throws FileNotFoundException {
        out = new BufferedOutputStream(new FileOutputStream(logFile));
        calendar = new GregorianCalendar();
    }
    
    public void log(String data) throws IOException {
        logToFile(String.format("%HH:MM:SS:LL:NN %s", calendar, data));
    }
    
    private void logToFile(String logString) throws IOException {
        out.write(String.format("%s%n", logString).getBytes());
    }
    
    public void finish() throws IOException {
        out.close();
    }
    
    private static long startTime = System.nanoTime();
    public void logSimpleTime(String data) throws IOException {
        logToFile(String.format("%14d : %s",Math.abs(System.nanoTime()-startTime), data));
    }
}

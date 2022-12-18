package com.example.sae32;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class LoggerView extends Handler {
    final private TextView logview;

    public LoggerView(TextView view, Level level){
        super();
        setLevel(level);
        setFormatter(new Formatter() {
            final private SimpleDateFormat dateformatter= new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.FRANCE);
            @Override
            public String format(LogRecord logRecord) {
                StringBuilder builder = new StringBuilder();
                builder.append(dateformatter.format(new Date(logRecord.getMillis()))).append(" - ");
                builder.append(logRecord.getLevel()).append(" - ");
                builder.append(logRecord.getMessage()).append("\n");
                return builder.toString();
            }
        });
        logview=view;
    }

    public void setVisibility(int visibility){
        logview.setVisibility(visibility);
    }
    @Override
    public synchronized void publish(LogRecord record){
        logview.append(getFormatter().format(record));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}

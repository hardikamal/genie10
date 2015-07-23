package com.supergenieapp.android.Extras;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class LoggingBuilder {
    Context context;
    File file;
    String className;
    boolean writeToLog;
    boolean canDisplayOnLogCat;

    public LoggingBuilder(Context context) {
        this.context = context;
    }

    private String getClassName() {
        return className;
    }

    public LoggingBuilder setClassName(String className) {
        this.className = className;
        return this;
    }

    private Context getContext() {
        return context;
    }

    private File getFile() {
        return file;
    }

    public LoggingBuilder setFile(String fileName) {
        this.file = new File(fileName);
        return this;
    }

    private boolean getWriteToLog() {
        return writeToLog;
    }

    public LoggingBuilder setWriteToLog(boolean writeToLog) {
        this.writeToLog = writeToLog;
        return this;
    }

    private boolean getCanDisplayOnLogCat() {
        return canDisplayOnLogCat;
    }

    public LoggingBuilder setCanDisplayOnLogCat(boolean canDisplayOnLogCat) {
        this.canDisplayOnLogCat = canDisplayOnLogCat;
        return this;
    }

    public Logging setUp() {
        setupDirectories();
        return new Logging(getContext(), getFile(), getClassName(), getWriteToLog(), getCanDisplayOnLogCat());
    }

    private void setupDirectories() {
        if (file == null)
            setFile("AppLog.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists() && getWriteToLog()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

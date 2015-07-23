package com.supergenieapp.android.Extras;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {
    Context context;
    File file;
    String className;
    boolean writeToLog;
    boolean canDisplayOnLog;
    DateFormat dateFormat;
    Date date;

    public Logging(Context context, File file, String className, boolean writeToLog, boolean canDisplayOnLog) {
        this.context = context;
        this.file = file;
        this.className = className;
        this.writeToLog = writeToLog;
        this.canDisplayOnLog = canDisplayOnLog;
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.date = new Date();
    }

    private void WriteIntoFile(String appendData) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file.getAbsolutePath(), true);
            writer.append(dateFormat.format(date)+" | "+appendData + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteToFile(File file, String appendData) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file.getAbsolutePath());
            writer.append(appendData);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LogD(String tagName, String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.d(this.className + " " + tagName, message);
            } else {
                Log.d(tagName, message);
            }
        }
        write(" DEBUG " + tagName, message);
    }

    public void LogD(String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.d(this.className, message);
            } else {
                Log.d(DataFields.TAG, message);
            }
        }
        write(" DEBUG " + DataFields.TAG, message);
    }

    public void LogV(String tagName, String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.v(this.className + " " + tagName, message);
            } else {
                Log.v(tagName, message);
            }
        }
        write(" VERBOSE " + tagName, message);
    }

    public void LogV(String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.v(this.className, message);
            } else {
                Log.v(DataFields.TAG, message);
            }
        }
        write(" VERBOSE " + DataFields.TAG, message);
    }

    public void LogI(String tagName, String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.i(this.className + " " + tagName, message);
            } else {
                Log.i(tagName, message);
            }
        }
        write(" INFO " + tagName, message);
    }

    public void LogI(String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.i(this.className, message);
            } else {
                Log.i(DataFields.TAG, message);
            }
        }
        write(" INFO " + DataFields.TAG, message);
    }

    public void LogE(String tagName, String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.e(this.className + " " + tagName, message);
            } else {
                Log.e(tagName, message);
            }
        }
        write(" ERROR " + tagName, message);
    }

    public void LogE(String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.e(this.className, message);
            } else {
                Log.e(DataFields.TAG, message);
            }
        }
        write(" ERROR " + DataFields.TAG, message);
    }

    public void LogW(String tagName, String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.w(this.className + " " + tagName, message);
            } else {
                Log.w(tagName, message);
            }
        }
        write(" WARNING " + tagName, message);
    }

    public void LogW(String message) {
        if (canDisplayOnLog) {
            if (this.className != null) {
                Log.w(this.className, message);
            } else {
                Log.w(DataFields.TAG, message);
            }
        }
        write(" WARNING " + DataFields.TAG, message);
    }

    private void write(String tagName, String message) {
        if (writeToLog) {
            if (this.className != null) {
                WriteIntoFile(this.className + " " + tagName + " " + message);
            } else {
                WriteIntoFile(tagName + " " + message);
            }
        }
    }
}
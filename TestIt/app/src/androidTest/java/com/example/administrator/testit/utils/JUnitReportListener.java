package com.example.administrator.testit.utils;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

/**
 * Created by vivian on 2016/2/18.
 */
public class JUnitReportListener implements TestListener {
    private static final String LOG_TAG = JUnitReportListener.class.getSimpleName();
    private static final String ENCODING_UTF_8 = "utf-8";
    public static final String TOKEN_SUITE = "__suite__";
    public static final String TOKEN_EXTERNAL = "__external__";
    private static final String TAG_SUITES = "testsuites";
    private static final String TAG_SUITE = "testsuite";
    private static final String TAG_CASE = "testcase";
    private static final String TAG_ERROR = "error";
    private static final String TAG_FAILURE = "failure";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_CLASS = "classname";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_MESSAGE = "message";
    private static final String ATTRIBUTE_TIME = "time";
    private static final String[] DEFAULT_TRACE_FILTERS = new String[]{"junit.framework.TestCase", "junit.framework.TestResult", "junit.framework.TestSuite", "junit.framework.Assert.", "java.lang.reflect.Method.invoke(", "sun.reflect.", "org.junit.", "junit.framework.JUnit4TestAdapter", " more", "android.test.", "android.app.Instrumentation", "java.lang.reflect.Method.invokeNative"};
    private Context mTargetContext;
    private String mReportFile;
    private String mReportDir;
    private boolean mFilterTraces;
    private boolean mMultiFile;
    private FileOutputStream mOutputStream;
    private XmlSerializer mSerializer;
    private String mCurrentSuite;
    private boolean mTimeAlreadyWritten = false;
    private long mTestStartTime;

    public JUnitReportListener(Context context, Context targetContext, String reportFile, String reportDir, boolean filterTraces, boolean multiFile) {
        Log.i(LOG_TAG, "Listener created with arguments:\n  report file  : \'" + reportFile + "\'\n" + "  report dir   : \'" + reportDir + "\'\n" + "  filter traces: " + filterTraces + "\n" + "  multi file   : " + multiFile);
        this.mTargetContext = targetContext;
        this.mReportFile = reportFile;
        this.mReportDir = reportDir;
        this.mFilterTraces = filterTraces;
        this.mMultiFile = multiFile;
    }

    public void startTest(Test test) {
        try {
            if(test instanceof TestCase) {
                TestCase e = (TestCase)test;
                this.checkForNewSuite(e);
                this.mSerializer.startTag("", "testcase");
                this.mSerializer.attribute("", "classname", this.mCurrentSuite);
                this.mSerializer.attribute("", "name", e.getName());
                this.mTimeAlreadyWritten = false;
                this.mTestStartTime = System.currentTimeMillis();
            }
        } catch (IOException var3) {
            Log.e(LOG_TAG, this.safeMessage(var3));
        }

    }

    private void checkForNewSuite(TestCase testCase) throws IOException {
        String suiteName = testCase.getClass().getName();
        if(this.mCurrentSuite == null || !this.mCurrentSuite.equals(suiteName)) {
            if(this.mCurrentSuite != null) {
                if(this.mMultiFile) {
                    this.close();
                } else {
                    this.mSerializer.endTag("", "testsuite");
                    this.mSerializer.flush();
                }
            }

            this.openIfRequired(suiteName);
            this.mSerializer.startTag("", "testsuite");
            this.mSerializer.attribute("", "name", suiteName);
            this.mCurrentSuite = suiteName;
        }

    }

    private void openIfRequired(String suiteName) {
        try {
            if(this.mSerializer == null) {
                this.mOutputStream = this.openOutputStream(this.resolveFileName(suiteName));
                this.mSerializer = Xml.newSerializer();
                this.mSerializer.setOutput(this.mOutputStream, "utf-8");
                this.mSerializer.startDocument("utf-8", Boolean.valueOf(true));
                if(!this.mMultiFile) {
                    this.mSerializer.startTag("", "testsuites");
                }
            }

        } catch (IOException var3) {
            Log.e(LOG_TAG, this.safeMessage(var3));
            throw new RuntimeException("Unable to open serializer: " + var3.getMessage(), var3);
        }
    }

    private String resolveFileName(String suiteName) {
        String fileName = this.mReportFile;
        if(this.mMultiFile) {
            fileName = fileName.replace("__suite__", suiteName);
        }

        return fileName;
    }

    private FileOutputStream openOutputStream(String fileName) throws IOException {
        if(this.mReportDir == null) {
            Log.d(LOG_TAG, "No reportDir specified. Opening report file \'" + fileName + "\' in internal storage of app under test");
            return this.mTargetContext.openFileOutput(fileName, 1);
        } else {
            File outputFile;
            if(this.mReportDir.contains("__external__")) {
                outputFile =Compatibility.getExternalFilesDir(this.mTargetContext, (String)null);
                if(outputFile == null) {
                    Log.e(LOG_TAG, "reportDir references external storage, but external storage is not available (check mounting and permissions)");
                    throw new IOException("Cannot access external storage");
                }

                String externalPath = outputFile.getAbsolutePath();
                if(externalPath.endsWith("/")) {
                    externalPath = externalPath.substring(0, externalPath.length() - 1);
                }

                this.mReportDir = this.mReportDir.replace("__external__", externalPath);
            }

            this.ensureDirectoryExists(this.mReportDir);
            outputFile = new File(this.mReportDir, fileName);
            Log.d(LOG_TAG, "Opening report file \'" + outputFile.getAbsolutePath() + "\'");
            return new FileOutputStream(outputFile);
        }
    }

    private void ensureDirectoryExists(String path) throws IOException {
        File dir = new File(path);
        if(!dir.isDirectory() && !dir.mkdirs()) {
            String message = "Cannot create directory \'" + path + "\'";
            Log.e(LOG_TAG, message);
            throw new IOException(message);
        }
    }

    public void addError(Test test, Throwable error) {
        this.addProblem("error", error);
    }

    public void addFailure(Test test, AssertionFailedError error) {
        this.addProblem("failure", error);
    }

    private void addProblem(String tag, Throwable error) {
        try {
            this.recordTestTime();
            this.mSerializer.startTag("", tag);
            this.mSerializer.attribute("", "message", this.safeMessage(error));
            this.mSerializer.attribute("", "type", error.getClass().getName());
            StringWriter e = new StringWriter();
            error.printStackTrace((PrintWriter)(this.mFilterTraces?new JUnitReportListener.FilteringWriter(e):new PrintWriter(e)));
            this.mSerializer.text(e.toString());
            this.mSerializer.endTag("", tag);
            this.mSerializer.flush();
        } catch (IOException var4) {
            Log.e(LOG_TAG, this.safeMessage(var4));
        }

    }

    private void recordTestTime() throws IOException {
        if(!this.mTimeAlreadyWritten) {
            this.mTimeAlreadyWritten = true;
            this.mSerializer.attribute("", "time", String.format(Locale.ENGLISH, "%.3f", new Object[]{Double.valueOf((double)(System.currentTimeMillis() - this.mTestStartTime) / 1000.0D)}));
        }

    }

    public void endTest(Test test) {
        try {
            if(test instanceof TestCase) {
                this.recordTestTime();
                this.mSerializer.endTag("", "testcase");
                this.mSerializer.flush();
            }
        } catch (IOException var3) {
            Log.e(LOG_TAG, this.safeMessage(var3));
        }

    }

    public void close() {
        if(this.mSerializer != null) {
            try {
                if("testcase".equals(this.mSerializer.getName())) {
                    this.mSerializer.endTag("", "testcase");
                }

                if(this.mCurrentSuite != null) {
                    this.mSerializer.endTag("", "testsuite");
                }

                if(!this.mMultiFile) {
                    this.mSerializer.endTag("", "testsuites");
                }

                this.mSerializer.endDocument();
                this.mSerializer.flush();
                this.mSerializer = null;
            } catch (IOException var3) {
                Log.e(LOG_TAG, this.safeMessage(var3));
            }
        }

        if(this.mOutputStream != null) {
            try {
                this.mOutputStream.close();
                this.mOutputStream = null;
            } catch (IOException var2) {
                Log.e(LOG_TAG, this.safeMessage(var2));
            }
        }

    }

    private String safeMessage(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getName() + ": " + (message == null?"<null>":message);
    }

    private static class FilteringWriter extends PrintWriter {
        public FilteringWriter(Writer out) {
            super(out);
        }

        public void println(String s) {
            String[] arr$ = JUnitReportListener.DEFAULT_TRACE_FILTERS;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String filtered = arr$[i$];
                if(s.contains(filtered)) {
                    return;
                }
            }

            super.println(s);
        }
    }
}

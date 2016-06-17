package com.example.administrator.testit.utils;

import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.util.Log;


/**
 * Created by vivian on 2016/2/18.
 */
public class JUnitReportTestRunner extends InstrumentationTestRunner {
    private static final String ARG_REPORT_FILE = "reportFile";
    private static final String ARG_REPORT_DIR = "reportDir";
    private static final String ARG_FILTER_TRACES = "filterTraces";
    private static final String ARG_MULTI_FILE = "multiFile";
    private static final String DEFAULT_SINGLE_REPORT_FILE = "junit-report.xml";
    private static final String DEFAULT_MULTI_REPORT_FILE = "junit-report-__suite__.xml";
    private static final String LOG_TAG = JUnitReportTestRunner.class.getSimpleName();
    private JUnitReportListener mListener;
    private String mReportFile;
    private String mReportDir;
    private boolean mFilterTraces = true;
    private boolean mMultiFile = false;

    public JUnitReportTestRunner() {
    }

    public void onCreate(Bundle arguments) {
        if(arguments != null) {
            Log.i(LOG_TAG, "Created with arguments: " + arguments.keySet());
            this.mReportFile = arguments.getString("reportFile");
            this.mReportDir = arguments.getString("reportDir");
            this.mFilterTraces = this.getBooleanArgument(arguments, "filterTraces", true);
            this.mMultiFile = this.getBooleanArgument(arguments, "multiFile", false);
        } else {
            Log.i(LOG_TAG, "No arguments provided");
        }

        if(this.mReportFile == null) {
            this.mReportFile = this.mMultiFile?"junit-report-__suite__.xml":"junit-report.xml";
            Log.i(LOG_TAG, "Defaulted report file to \'" + this.mReportFile + "\'");
        }

        super.onCreate(arguments);
    }

    private boolean getBooleanArgument(Bundle arguments, String name, boolean defaultValue) {
        String value = arguments.getString(name);
        return value == null?defaultValue:Boolean.parseBoolean(value);
    }

    protected AndroidTestRunner makeAndroidTestRunner() {
        return new AndroidTestRunner();
    }

    protected AndroidTestRunner getAndroidTestRunner() {
        AndroidTestRunner runner = this.makeAndroidTestRunner();
        this.mListener = new JUnitReportListener(this.getContext(), this.getTargetContext(), this.mReportFile, this.mReportDir, this.mFilterTraces, this.mMultiFile);
        runner.addTestListener(this.mListener);
        return runner;
    }

    public void finish(int resultCode, Bundle results) {
        if(this.mListener != null) {
            this.mListener.close();
        }

        super.finish(resultCode, results);
    }
}

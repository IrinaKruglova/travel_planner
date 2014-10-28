package com.toptal.travelplanner;

import android.app.Application;
import android.os.AsyncTask;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public static volatile boolean taskCompleted = false;
    public static volatile boolean taskResult = false;

    public ApplicationTest() {
        super(Application.class);

        TestTask task = new TestTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        while (!taskCompleted) { }
        assertTrue(taskResult);
    }
}
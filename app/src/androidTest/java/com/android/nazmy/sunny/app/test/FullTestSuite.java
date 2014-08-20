package com.android.nazmy.sunny.app.test;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by nazmy on 8/20/14.
 */
public class FullTestSuite extends  TestSuite {
    public static Test Suite() {
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public FullTestSuite() {
        super();
    }
}

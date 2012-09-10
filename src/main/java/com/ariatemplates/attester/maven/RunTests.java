/*
 * Copyright 2012 Amadeus s.a.s.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ariatemplates.attester.maven;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Runs <a href="https://github.com/ariatemplates/attester#usage">attester</a>.
 * This usually executes tests in PhantomJS and generates reports.
 *
 * @goal test
 * @phase test
 */
public class RunTests extends RunAttester {

    /**
     * If true, the execution of this goal is skipped (it is bound to the
     * maven.test.skip variable).
     *
     * @parameter expression="${maven.test.skip}"
     */
    public boolean skip;

    /**
     * If true, the execution of this goal is skipped (it is bound to the
     * skipTests variable).
     *
     * @parameter expression="${skipTests}"
     */
    public boolean skipTests;

    /**
     * If true, errors during the tests (not including failures) will not make
     * the build fail. (Passes <code>--ignore-errors</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     *
     * @parameter expression="${maven.test.error.ignore}"
     */
    public boolean ignoreErrors;

    /**
     * If true, failures (anticipated errors) during the tests will not make the
     * build fail. (Passes <code>--ignore-failures</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     *
     * @parameter expression="${maven.test.failure.ignore}"
     */
    public boolean ignoreFailures;

    /**
     * Number of PhantomJS instances to start, to execute tests in parallel. <br/>
     * (Passed through <code>--phantomjs-instances</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     *
     * @parameter expression="${attester.phantomJSInstances}" default-value=2
     */
    public int phantomjsInstances;

    /**
     * Path to the PhantomJS executable. If not defined, and phantomjsInstances
     * &gt; 0, PhantomJS is extracted from the the following maven artifact:
     * <code>com.google.code.phantomjs:phantomjs:1.6.0:zip:win32-static</code> <br/>
     * (Passed through <code>--phantomjs-path</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     *
     * @parameter expression="${com.google.code.phantomjs.path}"
     */
    public File phantomjsPath;

    protected void testBanner() {
        PrintStream out = System.out;
        out.println("");
        out.println("-------------------------------------------------------");
        out.println(" T E S T S   -   J A V A S C R I P T");
        out.println("-------------------------------------------------------");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip || skipTests) {
            getLog().info("Skipping tests.");
            return;
        }
        if (phantomjsInstances > 0) {
            extractPhantomJS();
        }
        super.execute();
    }

    @Override
    protected void redirectNodeStreams() {
        testBanner();
        super.redirectNodeStreams();
    }

    @Override
    protected void addExtraAttesterOptions(List<String> list) {
        if (ignoreErrors) {
            list.add("--ignore-errors");
        }
        if (ignoreFailures) {
            list.add("--ignore-failures");
        }
        if (phantomjsInstances > 0) {
            list.add("--phantomjs-path");
            list.add(phantomjsExecutable.getAbsolutePath());
        }

        list.add("--phantomjs-instances");
        list.add(String.valueOf(phantomjsInstances));

        super.addExtraAttesterOptions(list);
    }

    protected void extractPhantomJS() {
        Dependency phantomJSdependency = getPhantomJSDependency();
        // String directoryInZip = phantomJSdependency.getArtifactId() + "-" +
        // phantomJSdependency.getVersion() ;
        String directoryInZip = phantomJSdependency.getArtifactId() + "-1.6.1";
        phantomjsExecutable = extractDependency(phantomjsPath, phantomJSdependency, "", directoryInZip + File.separator + "phantomjs.exe");
    }

    public static Dependency getPhantomJSDependency() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("com.google.code.phantomjs");
        dependency.setArtifactId("phantomjs");
        dependency.setVersion("1.6.0");
        dependency.setClassifier("win32-static");
        dependency.setType("zip");
        return dependency;
    }

}

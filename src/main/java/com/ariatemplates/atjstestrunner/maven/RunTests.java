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

package com.ariatemplates.atjstestrunner.maven;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal test
 * @phase test
 */
public class RunTests extends RunATJSTestRunner {

    /**
     * @parameter expression="${maven.test.skip}"
     */
    public boolean skip;

    /**
     * @parameter expression="${skipTests}"
     */
    public boolean skipTests;

    /**
     * @parameter expression="${maven.test.error.ignore}"
     */
    public boolean ignoreErrors;

    /**
     * @parameter expression="${maven.test.failure.ignore}"
     */
    public boolean ignoreFailures;

    /**
     * @parameter expression="${atjstestrunner.phantomJSInstances}"
     *            default-value=2
     */
    public int phantomjsInstances;

    /**
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
    protected void addExtraAtjstestrunnerOptions(List<String> list) {
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

        super.addExtraAtjstestrunnerOptions(list);
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

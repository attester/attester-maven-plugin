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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
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
     * &gt; 0, PhantomJS is used from the the following maven artifact:
     * <code>com.google.code.phantomjs:phantomjs:1.9.7:exe:win32</code> <br/>
     * (Passed through <code>--phantomjs-path</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     *
     * @parameter expression="${com.google.code.phantomjs.path}"
     */
    public File phantomjsPath;

    /**
     * Path to an attester-launcher configuration file. <br/>
     * (Passed through <code>--launcher-config</code> to <a
     * href="https://github.com/ariatemplates/attester#usage">attester</a>).
     * @parameter expression="${attester.launcher.config}"
     */
    public File launcherConfig;

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
        findPhantomjs();
        list.add("--phantomjs-path");
        list.add(phantomjsPath.getAbsolutePath());

        list.add("--phantomjs-instances");
        list.add(String.valueOf(phantomjsInstances));

        if (launcherConfig != null) {
            list.add("--launcher-config");
            list.add(launcherConfig.getAbsolutePath());
        }

        super.addExtraAttesterOptions(list);
    }

    protected void findPhantomjs() {
        if (phantomjsPath == null) {
            Artifact phantomjsArtifact = new DefaultArtifact("com.google.code.phantomjs", "phantomjs", "1.9.7", "runtime", "exe", "win32",
                    new DefaultArtifactHandler("exe"));
            phantomjsArtifact = session.getLocalRepository().find(phantomjsArtifact);
            phantomjsPath = phantomjsArtifact.getFile();
        }
    }
}

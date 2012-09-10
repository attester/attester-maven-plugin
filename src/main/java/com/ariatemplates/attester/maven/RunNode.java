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

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Runs a node.js command. An error is raised if the exit code is different from
 * 0.
 *
 * @goal node
 */
public class RunNode extends AbstractMojo {

    private static final String START_FAILED = "Failed to start node.js.\nThe following executable was used: %s";
    private static final String NODEJS_PATH_PROPERTY = "org.nodejs.node.path";

    /**
     * Arguments to pass to the node.js process.
     *
     * @parameter
     */
    public List<String> arguments = new LinkedList<String>();

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    protected MavenSession session;

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    protected Process nodeProcess;

    protected Thread shutdownHook = new Thread(new Runnable() {
        public void run() {
            closeNodeProcess();
        }
    });

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            createNodeProcess();
            redirectNodeStreams();
            int returnValue = waitForNodeToExit();
            if (returnValue != 0) {
                throw new MojoFailureException("Errors happened.");
            }
        } finally {
            // when exiting the execute method for whatever reason, all external
            // processes must be finished
            closeNodeProcess();
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
        }
    }

    protected void closeNodeProcess() {
        try {
            nodeProcess.exitValue();
        } catch (IllegalThreadStateException e) {
            nodeProcess.destroy();
            System.out.println("Terminated the node process.");
        }
    }

    protected String getNodeExecutable() {
        String nodeJsPath = System.getProperty(NODEJS_PATH_PROPERTY);
        if (nodeJsPath == null) {
            Artifact nodeArtifact = new DefaultArtifact("org.nodejs", "node", "0.8.3", "runtime", "exe", "win32", new DefaultArtifactHandler("exe"));
            nodeArtifact = session.getLocalRepository().find(nodeArtifact);
            nodeJsPath = nodeArtifact.getFile().getAbsolutePath();
        }
        return nodeJsPath;
    }

    protected void createNodeProcess() {
        List<String> nodeArguments = getNodeArguments();
        String nodeExecutable = getNodeExecutable();
        nodeArguments.add(0, nodeExecutable);
        getLog().info(String.format("Starting node.js: %s", nodeArguments.toString()));
        try {
            String[] argumentsArray = nodeArguments.toArray(new String[0]);
            nodeProcess = Runtime.getRuntime().exec(argumentsArray);
        } catch (Exception e) {
            if (nodeProcess != null) {
                closeNodeProcess();
                nodeProcess = null;
            }
            throw new RuntimeException(String.format(START_FAILED, nodeExecutable), e);
        }
    }

    protected void redirectNodeStreams() {
        StreamRedirector.redirectStream(nodeProcess.getInputStream(), System.out);
        StreamRedirector.redirectStream(nodeProcess.getErrorStream(), System.err);
    }

    protected int waitForNodeToExit() {
        try {
            return nodeProcess.waitFor();
        } catch (InterruptedException e) {
            return -1;
        }
    }

    protected List<String> getNodeArguments() {
        return arguments;
    }

}

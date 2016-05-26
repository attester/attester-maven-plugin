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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;

/**
 * Extracts the zip dependencies of this plugin in a temporary directory named
 * according to the artifact. If a dependency is already extracted, it is not
 * extracted again.
 *
 * @goal extract-dependencies
 */
public class ArtifactExtractor extends AbstractMojo {

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     *
     */
    protected MavenSession session;

    /**
     * @parameter expression="${project}"
     * @readonly
     * @required
     *
     */
    protected MavenProject project;

    /**
     * Parent directory in which directories containing the extracted artifacts
     * will be created. If this parameter is not set, the temporary directory
     * defined in the <code>java.io.tmpdir</code> system property is used.
     *
     * @parameter
     */
    public File outputParentDirectory;

    /**
     * If set to true, files will be extracted directly in their final destination folder.
     * Otherwise, a temporary folder will first be created, then files will be extracted inside
     * that folder, and, finally, the folder will be renamed.
     *
     * @parameter
     */
    public boolean avoidRenamingTargetDirectory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Map<?, ?> context = getPluginContext();
            PluginDescriptor pluginDescriptor = (PluginDescriptor) context.get("pluginDescriptor");
            Model projectModel = project.getModel();
            for (Dependency dependency : pluginDescriptor.getPlugin().getDependencies()) {
                if ("zip".equalsIgnoreCase(dependency.getType())) {
                    String outputDirectory = inplaceExtractDependency(session.getLocalRepository(), dependency);
                    projectModel.addProperty(getPropertyName(dependency), outputDirectory);
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error", e);
        }
    }

    public static String getPropertyName(Dependency dependency) {
        return "artifact.extracted:" + dependency.getManagementKey();
    }

    public static File getOutputDirectory(Artifact artifact) {
        return getOutputDirectory(artifact, null);
    }

    public static File getOutputDirectory(Artifact artifact, File parentDirectory) {
        if (parentDirectory == null) {
            parentDirectory = new File(System.getProperty("java.io.tmpdir"));
        }
        String name = artifact.getGroupId() + "-" + artifact.getArtifactId() + "-" + artifact.getVersion() + "-" + artifact.getClassifier();
        if (artifact.isSnapshot()) {
            name += "-" + artifact.getFile().lastModified();
        }
        File res = new File(parentDirectory, name);
        return res;
    }

    private boolean renameDirectory(File tempDirectory, File outputDirectory) throws InterruptedException {
        for (int remainingAttempts = 20; remainingAttempts > 0; remainingAttempts--) {
            if (outputDirectory.exists()) {
                // the output directory was created in the mean time
                // (probably by another build running in parallel)
                getLog().info("Another process probably extracted the artifact at the same time.");
                return true;
            } else if (tempDirectory.renameTo(outputDirectory)) {
                getLog().info("Renaming the folder succeeded!");
                return true;
            }
            // sleep a bit and retry, the failure may be temporary (file opened
            // by an anti-virus program...)
            getLog().warn("Renaming the folder failed! Will try again in 250ms...");
            Thread.sleep(250);
        }
        return false;
    }

    public String inplaceExtractDependency(ArtifactRepository localRepository, Dependency dependency) throws Exception {
        Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), dependency.getScope(),
            dependency.getType(), dependency.getClassifier(), new DefaultArtifactHandler("zip"));
        artifact = localRepository.find(artifact);
        File zipFile = artifact.getFile();
        File outputDirectory = getOutputDirectory(artifact, outputParentDirectory);
        if (outputDirectory.exists()) {
            // if the directory already exists, don't touch it (by the way, it
            // may currently be in use by some other process)
            getLog().info("Artifact " + artifact + " is already extracted in " + outputDirectory);
        } else {
            getLog().info("Extracting artifact " + artifact + " to " + outputDirectory);
            File tempDirectory = null;
            try {
                if (avoidRenamingTargetDirectory) {
                    unzip(zipFile, outputDirectory);
                } else {
                    // temporary directory where to extract
                    tempDirectory = File.createTempFile("extract", null, outputDirectory.getParentFile());
                    tempDirectory.delete(); // delete the file created by
                                            // createTempFile
                                            // (to replace it with a directory)
                    unzip(zipFile, tempDirectory);
                    // Move the temporary directory to its final location
                    if (!renameDirectory(tempDirectory, outputDirectory)) {
                        throw new Exception("Failed to rename directory " + tempDirectory + " to " + outputDirectory + ".");
                    }
                }
            } catch (Exception e) {
                getLog().error("An exception occurred while extracting the " + artifact + " artifact.", e);
                throw e;
            } finally {
                if (tempDirectory != null && tempDirectory.exists()) {
                    getLog().info("Deleting temporary directory " + tempDirectory);
                    FileUtils.deleteQuietly(tempDirectory);
                }
            }
        }
        return outputDirectory.getAbsolutePath();
    }

    public static void unzip(File zipFile, File outputFolder) throws IOException {
        ZipInputStream zipInputStream = null;
        try {
            ZipEntry entry = null;
            zipInputStream = new ZipInputStream(FileUtils.openInputStream(zipFile));
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File outputFile = new File(outputFolder, entry.getName());

                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                    continue;
                }

                OutputStream outputStream = null;
                try {
                    outputStream = FileUtils.openOutputStream(outputFile);
                    IOUtils.copy(zipInputStream, outputStream);
                    outputStream.close();
                } catch (IOException exception) {
                    outputFile.delete();
                    throw new IOException(exception);
                } finally {
                    IOUtils.closeQuietly(outputStream);
                }
            }
            zipInputStream.close();
        } finally {
            IOUtils.closeQuietly(zipInputStream);
        }
    }

}

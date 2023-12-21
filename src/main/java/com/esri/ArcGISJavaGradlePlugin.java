/*
 * Copyright 2023 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.esri;

import java.nio.file.Path;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Copy;

/**
 * Gradle plugin for managing the ArcGIS Maps SDK for Java.
 */
public class ArcGISJavaGradlePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // Create extension so user can set arcgis version in build.gradle
    ArcGISJavaGradlePluginExtension arcGISExtension = project.getExtensions().create(
        "arcgis", ArcGISJavaGradlePluginExtension.class);

    // Add and configure the Maven repository containing the ArcGIS artifacts
    project.getRepositories().maven((mavenArtifactRepository) -> {
      mavenArtifactRepository.setUrl("https://esri.jfrog.io/artifactory/arcgis");
    });

    // Register and configure copyArcGISNatives task
    project.getConfigurations().register("natives");
    var copyNatives = project.getTasks().register("copyArcGISNatives", Copy.class);
    copyNatives.configure(copyNativesTask -> {
      copyNativesTask.setDescription("Copies the ArcGIS native libraries into the .arcgis directory for development.");
      copyNativesTask.setGroup("build");
      project.getConfigurations().getByName("natives").getAsFileTree().forEach(
          file -> copyNativesTask.from(project.zipTree(file)));
      copyNativesTask.into(Path.of(System.getProperty("user.home"), ".arcgis", arcGISExtension.getVersion().get()));
    });

    // After the project is evaluated...
    project.afterEvaluate(projectAfterEvaluation -> {
      // If there's a run task, add copyArcGISNatives task as a dependency to it
      Task runTask = project.getTasks().findByName("run");
      if (runTask != null) {
        runTask.dependsOn("copyArcGISNatives");
      }

      // If there's a test task, add copyArcGISNatives task as a dependency to it
      Task testTask = project.getTasks().findByName("test");
      if (testTask != null) {
        testTask.dependsOn("copyArcGISNatives");
      }

      // Add ArcGIS dependencies
      var version = arcGISExtension.getVersion().get();
      projectAfterEvaluation.getDependencies().add("implementation", "com.esri.arcgisruntime:arcgis-java:" + version);
      projectAfterEvaluation.getDependencies().add("natives", "com.esri.arcgisruntime:arcgis-java-jnilibs:" + version);
      projectAfterEvaluation.getDependencies().add("natives", "com.esri.arcgisruntime:arcgis-java-resources:" + version);
    });
  }
}

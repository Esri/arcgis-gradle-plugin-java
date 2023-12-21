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

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.stream.Stream;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for {@link ArcGISJavaGradlePlugin}.
 */
public class ArcGISJavaGradlePluginTests {
  private static final String RESOURCES_FOLDER = "src/test/resources/";
  private static Path mTestProjectDir;

  /**
   * Before each test, create a temporary directory for the test project and copy the basic set of project files into
   * it.
   */
  @BeforeEach
  public void beforeEach() {
    // Create a temporary directory for the test project
    try {
      mTestProjectDir = Files.createTempDirectory("ArcGISJavaGradlePluginTests");
    } catch (IOException e) {
      fail("Failed to createTempDirectory", e);
    }
  }

  /**
   * After each test, delete the test project directory.
   */
  @AfterEach
  public void afterEach() {
    // Delete the test project directory
    deleteDirectory(mTestProjectDir);
  }

  /**
   * Tests successful build of a project that uses the ArcGIS Maps SDK for Java.
   */
  @Test
  public void testBuildMapsSdkProjectSucceedsWithGoodPlugin() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Execute the build task
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("build")
        .withPluginClasspath()
        .build();

    // Check the task succeeded
    TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":build")).getOutcome();
    assertEquals(TaskOutcome.SUCCESS, taskOutcome);
  }

  /**
   * Tests build failure when the ArcGIS plugin is missing.
   */
  @Test
  public void testBuildMapsSdkProjectFailsWithoutPlugin() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Copy a build.gradle in which the plugin is missing to the test project directory
    replaceFile("pluginMissing-build.gradle", mTestProjectDir.resolve("build.gradle"));

    // Execute the build task and check it failed
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("build")
        .withPluginClasspath()
        .buildAndFail();
    assertTrue(buildResult.getOutput().contains("error: package com.esri.arcgisruntime.mapping does not exist"));
  }

  /**
   * Tests build failure when no version is specified for the ArcGIS plugin.
   */
  @Test
  public void testBuildMapsSdkProjectFailsWithoutVersion() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Copy a build.gradle in which the plugin has no version specified to the test project directory
    replaceFile("versionMissing-build.gradle", mTestProjectDir.resolve("build.gradle"));

    // Execute the build task and check it failed
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("build")
        .withPluginClasspath()
        .buildAndFail();
    assertTrue(buildResult.getOutput().contains(
        "Cannot query the value of extension 'arcgis' property 'version' because it has no value available"));
  }

  /**
   * Tests build failure when an invalid version is specified for the ArcGIS plugin.
   */
  @Test
  public void testBuildMapsSdkProjectFailsWithInvalidVersion() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Copy a build.gradle in which the plugin has an invalid version specified to the test project directory
    replaceFile("versionInvalid-build.gradle", mTestProjectDir.resolve("build.gradle"));

    // Execute the build task and check it failed
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("build")
        .withPluginClasspath()
        .buildAndFail();
    assertTrue(buildResult.getOutput().contains("Could not find com.esri.arcgisruntime"));
  }

  /**
   * Tests successful running of a project that uses the ArcGIS Maps SDK for Java.
   */
  @Test
  public void testRunMapsSdkProjectSuccess() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Execute the run task
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("run")
        .withPluginClasspath()
        .build();

    // Check the task succeeded
    TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":run")).getOutcome();
    assertEquals(TaskOutcome.SUCCESS, taskOutcome);
    assertTrue(buildResult.getOutput().contains("Hello World!"));
  }

  /**
   * Tests that running a project fails if the app sets an install directory that does not contain the ArcGIS Maps SDK
   * for Java.
   */
  @Test
  public void testRunMapsSdkProjectApiNotFound() {
    // Copy all the basicMapsSdkProject files to the test project directory
    copyFileTree("basicMapsSdkProject", mTestProjectDir);

    // Copy an app that sets the InstallDirectory incorrectly to the test project directory
    replaceFile("badInstallDirectory-App.java", mTestProjectDir.resolve("src/main/java/demo/App.java"));

    // Execute the run task and check it failed
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("run")
        .withPluginClasspath()
        .buildAndFail();
    assertTrue(buildResult.getOutput().contains(
        "Could not find the API in the directory specified by calling ArcGISRuntimeEnvironment.setInstallDirectory"));
  }

  /**
   * Tests successful build of a library project that uses the ArcGIS Maps SDK for Java.
   */
  @Test
  public void testBuildLibraryProjectSucceedsWithGoodPlugin() {
    // Copy all the libraryProject files to the test project directory
    copyFileTree("libraryProject", mTestProjectDir);

    // Execute the build task
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("build")
        .withPluginClasspath()
        .build();

    // Check the task succeeded
    TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":lib:build")).getOutcome();
    assertEquals(TaskOutcome.SUCCESS, taskOutcome);
  }

  /**
   * Tests successful running of the tests in a library project that uses the ArcGIS Maps SDK for Java.
   */
  @Test
  public void testRunLibraryProjectTestsSuccess() {
    // Copy all the libraryProject files to the test project directory
    copyFileTree("libraryProject", mTestProjectDir);

    // Execute the test task
    BuildResult buildResult = GradleRunner.create()
        .withProjectDir(mTestProjectDir.toFile())
        .withArguments("test")
        .withPluginClasspath()
        .build();

    // Check the task succeeded
    TaskOutcome taskOutcome = Objects.requireNonNull(buildResult.task(":lib:test")).getOutcome();
    assertEquals(TaskOutcome.SUCCESS, taskOutcome);
  }

  /**
   * Deletes a directory, including all its contents.
   *
   * @param directory a Path representing the directory to be deleted
   */
  private static void deleteDirectory(Path directory) {
    // List the contents of the directory
    try (Stream<Path> contents = Files.list(directory)) {
      // Delete each file
      contents.forEach((path) -> {
        if (Files.isDirectory(path)) {
          deleteDirectory(path); // note recursive call to delete subdirectories
        } else {
          try {
            Files.delete(path);
          } catch (IOException e) {
            fail("Failed to delete directory", e);
          }
        }
      });

      // Delete the directory itself
      Files.delete(directory);
    } catch (IOException e) {
      fail("Failed to delete directory", e);
    }
  }

  /**
   * Copies all the files and directories in a given source directory to a given target path.
   *
   * @param sourceDirectoryName the name of the source directory within {@link #RESOURCES_FOLDER}
   * @param targetPath the target Path
   */
  private void copyFileTree(String sourceDirectoryName, Path targetPath) {
    try {
      // Walk the tree of the source directory, copying it all to the target path
      Path sourcePath = Paths.get(RESOURCES_FOLDER, sourceDirectoryName);
      Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
            throws IOException {
          // Copy the directory we're visiting to the target location
          Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
          try {
            Files.copy(dir, targetDir);
          } catch (FileAlreadyExistsException e) {
            if (!Files.isDirectory(targetDir))
              throw e;
          }
          return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
          // Copy the file we're visiting to the target location
          Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
          return CONTINUE;
        }
      });
    } catch (IOException e) {
      fail("Failed to install " + sourceDirectoryName, e);
    }
  }

  /**
   * Replaces the file at a given target Path with a given source file.
   *
   * @param sourceFileName name of source file, relative to {@link #RESOURCES_FOLDER}
   * @param targetPath path of file to be replaced
   */
  private void replaceFile(String sourceFileName, Path targetPath) {
    Path sourcePath = Paths.get(RESOURCES_FOLDER + sourceFileName);
    try {
      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      fail("Failed to replace " + targetPath.getFileName(), e);
    }
  }
}

package org.smoothbuild.acceptance;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.Files.createTempDir;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.ByteStreams;

public class AcceptanceTestCase {
  private static final String DEFAULT_BUILD_SCRIPT_FILE = "build.smooth";
  private static final String ARTIFACTS_DIR_PATH = ".smooth/artifacts/";

  private File tempDir;
  private Integer exitCode;
  private String outputData;

  @Before
  public void before() {
    tempDir = createTempDir();
  }

  @After
  public void after() throws IOException {
    deleteRecursively(projectDir().toPath());
  }

  protected void givenBuildScript(String buildScript) throws IOException {
    givenFile(DEFAULT_BUILD_SCRIPT_FILE, buildScript);
  }

  protected void givenFile(String path, String content) throws IOException {
    Path fullPath = Paths.get(projectDir().getPath(), path);
    Files.createDirectories(fullPath.getParent());
    try (FileWriter writer = new FileWriter(fullPath.toString())) {
      content.getBytes(UTF_8);
      writer.write(content);
    }
  }

  protected void givenDir(String path) throws IOException {
    Path fullPath = Paths.get(projectDir().getPath(), path);
    Files.createDirectories(fullPath);
  }

  protected void whenRunSmoothBuild(String command) {
    whenRunSmooth("build ", command);
  }

  protected void whenRunSmoothClean() {
    whenRunSmooth("clean ");
  }

  public void whenRunSmooth(String... smoothCommandArgs) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(smoothCommandArgs));
      processBuilder.directory(projectDir());
      Process process = processBuilder.start();
      drainDataFromErrorStream(process);
      outputData = readOutputData(process);
      exitCode = process.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImmutableList<String> processArgs(String... params) {
    Builder<String> builder = ImmutableList.builder();
    builder.add(smoothBinaryPath());
    builder.addAll(asList(params));
    return builder.build();
  }

  private static String smoothBinaryPath() {
    String smoothHome = System.getenv("smooth_home_dir");
    if (smoothHome == null) {
      throw new RuntimeException(
          "smooth_home_dir env variable not set, you should run tests via ant");
    }
    return smoothHome + "/smooth";
  }

  private String readOutputData(Process process) throws IOException {
    return inputStreamToString(process.getInputStream());
  }

  private void drainDataFromErrorStream(Process process) {
    final InputStream errorStream = new BufferedInputStream(process.getErrorStream());
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          copy(errorStream, ByteStreams.nullOutputStream());
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  protected void thenReturnedCode(int expected) {
    if (expected != exitCode.intValue()) {
      fail("Expected return code " + expected + " but was " + exitCode.intValue()
          + ".\nconsole output:\n" + outputData);
    }
  }

  protected void thenArtifact(String name, Matcher<File> matcher) {
    assertThat(artifactFile(name), matcher);
  }

  protected void thenPrinted(Matcher<String> matcher) {
    assertThat(outputData, matcher);
  }

  protected File artifactFile(String name) {
    return new File(projectDir(), ARTIFACTS_DIR_PATH + name);
  }

  protected File projectDir() {
    return tempDir;
  }

  public static String script(String string) {
    return string.replace('\'', '"');
  }

  protected String artifactContent(String artifact) throws IOException {
    return inputStreamToString(new FileInputStream(artifactFile(artifact)));
  }
}

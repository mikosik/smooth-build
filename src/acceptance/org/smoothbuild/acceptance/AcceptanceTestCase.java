package org.smoothbuild.acceptance;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.ByteStreams.copy;
import static com.google.common.io.Files.createTempDir;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_JAVA_EXCEPTION;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class AcceptanceTestCase {
  private static final String DEFAULT_BUILD_SCRIPT_FILE = "build.smooth";
  private static final String DEFAULT_NATIVE_JAR_FILE = "build.jar";
  private static final String ARTIFACTS_DIR_PATH = ".smooth/artifacts/";
  private static String SMOOTH_BINARY_PATH;
  private static String REPOSITORY_DIR;

  private File projectDir;
  private Integer exitCode;
  private String outputData;
  private String errorData;

  @Before
  public void before() {
    projectDir = createTempDir();
  }

  @After
  public void after() throws IOException {
    deleteRecursively(projectDir().toPath());
  }

  protected void givenScript(String buildScript) throws IOException {
    givenRawScript(buildScript.replace('\'', '"'));
  }

  protected void givenRawScript(String buildScript) throws IOException {
    givenFile(DEFAULT_BUILD_SCRIPT_FILE, buildScript);
  }

  protected void givenFile(String path, String content) throws IOException {
    File fullPath = file(path);
    fullPath.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(fullPath.toString())) {
      content.getBytes(UTF_8);
      writer.write(content);
    }
  }

  protected void givenNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(file(DEFAULT_NATIVE_JAR_FILE), classes);
  }

  protected void givenDir(String path) throws IOException {
    file(path).mkdirs();
  }

  protected void givenJunitCopied() {
    copyLib("junit-4.12.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = projectDir.toPath().resolve(dirInsideProject);
      destinationDir.toFile().mkdirs();
      return Files.copy(
          Paths.get(repositoryDir(), "lib/ivy/" + jar),
          destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected void whenSmoothBuild(String... args) {
    whenSmooth(join("build", args));
  }

  protected void whenSmoothClean(String... args) {
    whenSmooth(join("clean", args));
  }

  protected void whenSmoothHelp(String... args) {
    whenSmooth(join("help", args));
  }

  protected void whenSmoothList(String... args) {
    whenSmooth(join("list", args));
  }

  protected void whenSmoothVersion(String... args) {
    whenSmooth(join("version", args));
  }

  private static String[] join(String command, String[] args) {
    ArrayList<String> result = new ArrayList<>();
    result.add(command);
    result.addAll(asList(args));
    return result.toArray(new String[] {});
  }

  public void whenSmooth(String... smoothCommandArgs) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(smoothCommandArgs));
      processBuilder.directory(projectDir());
      Process process = processBuilder.start();
      ExecutorService executor = Executors.newFixedThreadPool(2);
      Future<ByteArrayOutputStream> inputStream =
          executor.submit(streamReadingCallable(process.getInputStream()));
      Future<ByteArrayOutputStream> errorStream =
          executor.submit(streamReadingCallable(process.getErrorStream()));
      exitCode = process.waitFor();
      outputData = new String(inputStream.get().toByteArray());
      errorData = new String(errorStream.get().toByteArray());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImmutableList<String> processArgs(String... params) {
    Builder<String> builder = ImmutableList.builder();
    builder.add(smoothBinaryPath());
    builder.addAll(asList(params));
    return builder.build();
  }

  private synchronized static String smoothBinaryPath() {
    initializePaths();
    return SMOOTH_BINARY_PATH;
  }

  private synchronized static String repositoryDir() {
    initializePaths();
    return REPOSITORY_DIR;
  }

  private static void initializePaths() {
    if (SMOOTH_BINARY_PATH == null) {
      String smoothHome = System.getenv("smooth_home_dir");
      String repositoryDir = System.getenv("repository_dir");
      if (smoothHome == null) {
        try {
          File repoDir = new File(".").getCanonicalFile();
          repositoryDir = repoDir.toString();
          ProcessBuilder processBuilder = new ProcessBuilder("ant", "install-smooth");
          processBuilder.directory(repoDir);
          Process process = processBuilder.start();
          ExecutorService executor = Executors.newFixedThreadPool(2);
          Future<ByteArrayOutputStream> inputStream =
              executor.submit(streamReadingCallable(process.getInputStream()));
          Future<ByteArrayOutputStream> errorStream =
              executor.submit(streamReadingCallable(process.getErrorStream()));
          int exitCode = process.waitFor();
          String outputData = new String(inputStream.get().toByteArray());
          String errorData = new String(errorStream.get().toByteArray());
          if (exitCode != 0) {
            throw new RuntimeException(
                "Running 'ant install-smooth' failed with following output\n"
                    + "STANDARD OUTPUT\n" + outputData + "\n"
                    + "STANDARD ERROR\n" + errorData + "\n");
          }
          smoothHome = repoDir.getAbsolutePath() + "/build/acceptance/smooth";
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(e);
        } catch (IOException e) {
          throw new RuntimeException(e);
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      }
      SMOOTH_BINARY_PATH = smoothHome + "/smooth";
      REPOSITORY_DIR = repositoryDir;
    }
  }

  private static Callable<ByteArrayOutputStream> streamReadingCallable(InputStream inputStream) {
    return () -> {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      copy(inputStream, outputStream);
      return outputStream;
    };
  }

  protected void thenFinishedWithSuccess() {
    thenReturnedCode(EXIT_CODE_SUCCESS);
  }

  protected void thenFinishedWithException() {
    thenReturnedCode(EXIT_CODE_JAVA_EXCEPTION);
  }

  protected void thenFinishedWithError() {
    thenReturnedCode(EXIT_CODE_ERROR);
  }

  private void thenReturnedCode(int expected) {
    if (expected != exitCode.intValue()) {
      fail("Expected return code " + expected + " but was " + exitCode.intValue() + ".\n"
          + "standard out:\n" + outputData + "\n"
          + "standard err:\n" + errorData + "\n");
    }
  }

  protected String output() {
    return outputData;
  }

  protected String error() {
    return errorData;
  }

  protected File artifact(String name) {
    return file(ARTIFACTS_DIR_PATH + name);
  }

  protected File file(String path) {
    return new File(projectDir(), path);
  }

  protected File projectDir() {
    return projectDir;
  }

  protected String artifactContent(String artifact) throws IOException {
    return inputStreamToString(new FileInputStream(artifact(artifact)));
  }
}

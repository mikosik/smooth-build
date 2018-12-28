package org.smoothbuild.acceptance;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Files.createTempDir;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_JAVA_EXCEPTION;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.acceptance.GitRepo.gitRepoRoot;
import static org.smoothbuild.acceptance.SmoothBinary.smoothBinary;
import static org.smoothbuild.cli.Commands.BUILD;
import static org.smoothbuild.cli.Commands.CLEAN;
import static org.smoothbuild.cli.Commands.DAG;
import static org.smoothbuild.cli.Commands.HELP;
import static org.smoothbuild.cli.Commands.LIST;
import static org.smoothbuild.cli.Commands.VERSION;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.googlecode.junittoolbox.ParallelRunner;

@RunWith(ParallelRunner.class)
public abstract class AcceptanceTestCase {
  private static final String DEFAULT_BUILD_SCRIPT_FILE = "build.smooth";
  private static final String DEFAULT_NATIVE_JAR_FILE = "build.jar";
  private static final String ARTIFACTS_DIR_PATH = ".smooth/artifacts/";
  private static final Path GIT_REPO_ROOT = gitRepoRoot();
  private static final Path SMOOTH_BINARY = smoothBinary(GIT_REPO_ROOT);

  private File projectDir;
  private Integer exitCode;
  private String outputData;
  private String errorData;

  @Before
  public void init() {
    projectDir = createTempDir();
  }

  @After
  public void destroy() throws IOException {
    deleteRecursively(projectDir().toPath());
  }

  public void givenScript(String buildScript) throws IOException {
    givenRawScript(buildScript.replace('\'', '"'));
  }

  public void givenRawScript(String buildScript) throws IOException {
    givenFile(DEFAULT_BUILD_SCRIPT_FILE, buildScript);
  }

  public void givenFile(String path, String content) throws IOException {
    File fullPath = file(path);
    fullPath.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(fullPath.toString())) {
      content.getBytes(UTF_8);
      writer.write(content);
    }
  }

  public void givenNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(file(DEFAULT_NATIVE_JAR_FILE), classes);
  }

  public void givenDir(String path) throws IOException {
    file(path).mkdirs();
  }

  public void givenJunitCopied() {
    copyLib("junit-4.12.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = projectDir.toPath().resolve(dirInsideProject);
      destinationDir.toFile().mkdirs();
      return Files.copy(
          GIT_REPO_ROOT.resolve("lib/ivy").resolve(jar),
          destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void whenSmoothBuild(String... args) {
    whenSmooth(concat(BUILD, args));
  }

  public void whenSmoothClean(String... args) {
    whenSmooth(concat(CLEAN, args));
  }

  public void whenSmoothDag(String... args) {
    whenSmooth(concat(DAG, args));
  }

  public void whenSmoothHelp(String... args) {
    whenSmooth(concat(HELP, args));
  }

  public void whenSmoothList(String... args) {
    whenSmooth(concat(LIST, args));
  }

  public void whenSmoothVersion(String... args) {
    whenSmooth(concat(VERSION, args));
  }

  public void whenSmooth(String... smoothCommandArgs) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(smoothCommandArgs));
      processBuilder.directory(projectDir());
      Process process = processBuilder.start();
      ExecutorService executor = Executors.newFixedThreadPool(2);
      Future<byte[]> inputStream = executor.submit(() -> toByteArray(process.getInputStream()));
      Future<byte[]> errorStream = executor.submit(() -> toByteArray(process.getErrorStream()));
      exitCode = process.waitFor();
      outputData = new String(inputStream.get());
      errorData = new String(errorStream.get());
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
    builder.add(SMOOTH_BINARY.toString());
    builder.addAll(list(params));
    return builder.build();
  }

  public void thenFinishedWithSuccess() {
    thenReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void thenFinishedWithException() {
    thenReturnedCode(EXIT_CODE_JAVA_EXCEPTION);
  }

  public void thenFinishedWithError() {
    thenReturnedCode(EXIT_CODE_ERROR);
  }

  private void thenReturnedCode(int expected) {
    if (expected != exitCode.intValue()) {
      fail("Expected return code " + expected + " but was " + exitCode.intValue() + ".\n"
          + "standard out:\n" + outputData + "\n"
          + "standard err:\n" + errorData + "\n");
    }
  }

  public void thenOutputContainsError(int lineNumber, String text) {
    thenOutputContains("build.smooth:" + lineNumber + ": error: " + text);
  }

  public void thenOutputContains(String text) {
    if (!outputData.contains(text)) {
      fail("Expected output to contain:\n"
          + text + "\n"
          + "but output was:\n"
          + outputData);
    }
  }

  public String output() {
    return outputData;
  }

  public String error() {
    return errorData;
  }

  public File artifact(String name) {
    return file(ARTIFACTS_DIR_PATH + name);
  }

  public Object artifactArray(String name) {
    try {
      return actual(artifact(name));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(File file) throws IOException {
    if (!file.exists()) {
      return null;
    }
    if (file.isDirectory()) {
      return actualArray(file);
    }
    return readAndClose(buffer(source(file)), s -> s.readString(CHARSET));
  }

  private static Object actualArray(File file) throws IOException {
    int count = file.list().length;

    List<Object> result = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      result.add(actual(new File(file, Integer.toString(i))));
    }
    return result;
  }

  public File file(String path) {
    return new File(projectDir(), path);
  }

  public File projectDir() {
    return projectDir;
  }

  public String artifactContent(String artifact) throws IOException {
    return readAndClose(buffer(source(artifact(artifact))), s -> s.readString(CHARSET));
  }
}

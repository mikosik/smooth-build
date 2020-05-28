package org.smoothbuild.acceptance;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Files.createTempDir;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.acceptance.AcceptanceUtils.GIT_REPO_ROOT;
import static org.smoothbuild.acceptance.AcceptanceUtils.SMOOTH_BINARY;
import static org.smoothbuild.acceptance.CommandWithArgs.buildCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.cleanCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.helpCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.listCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.treeCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.versionCommand;
import static org.smoothbuild.cli.console.Console.prefixMultiline;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_PATH;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.cli.Main;
import org.smoothbuild.util.DataReader;

import okio.BufferedSource;
import okio.ByteString;

public abstract class AcceptanceTestCase {
  private File projectDir;
  private Integer exitCode;
  private String sysOut;
  private String sysErr;

  @BeforeEach
  public void init() {
    init(createTempDir());
  }

  public void init(File projectDir) {
    checkState(this.projectDir == null, "init was already called");
    this.projectDir = projectDir;
  }

  @AfterEach
  public void destroy() throws IOException {
    deleteRecursively(projectDirAbsolute().toPath());
  }

  public void givenScript(String... lines) throws IOException {
    givenRawScript(quotesX2(join("\n", lines)));
  }

  public static String quotesX2(String string) {
    return string.replace('\'', '"');
  }

  public void givenRawScript(String buildScript) throws IOException {
    givenFile(USER_MODULE_PATH.toString(), buildScript);
  }

  public void givenFile(String path, String content) throws IOException {
    File fullPath = file(path);
    fullPath.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(fullPath.toString(), UTF_8)) {
      writer.write(content);
    }
  }

  public void givenNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(file("build.jar"), classes);
  }

  public void givenDir(String path) throws IOException {
    file(path).mkdirs();
  }

  public void givenJunitCopied() {
    copyLib("junit-4.13.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = projectDir.toPath().resolve(dirInsideProject);
      destinationDir.toFile().mkdirs();
      return Files.copy(GIT_REPO_ROOT.resolve("lib/ivy").resolve(jar), destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void whenSmoothBuild(String... args) {
    whenSmooth(buildCommand(args));
  }

  public void whenSmoothClean(String... args) {
    whenSmooth(cleanCommand(args));
  }

  public void whenSmoothHelp(String... args) {
    whenSmoothWithoutProjectAndInstallationDir(helpCommand(args));
  }

  public void whenSmoothList(String... args) {
    whenSmooth(listCommand(args));
  }

  public void whenSmoothTree(String... args) {
    whenSmooth(treeCommand(args));
  }

  public void whenSmoothVersion(String... args) {
    whenSmoothWithoutProjectDir(versionCommand(args));
  }

  public void whenSmoothWithoutProjectAndInstallationDir(CommandWithArgs command) {
    runSmooth(command);
  }

  public void whenSmoothWithoutProjectDir(CommandWithArgs command) {
    runSmooth(command,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  public void whenSmooth(CommandWithArgs command) {
    runSmooth(command,
        "--project-dir=" + projectDir,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  private void runSmooth(CommandWithArgs command, String... additionalArguments) {
    switch (AcceptanceUtils.TEST_MODE) {
      case SINGLE_JVM:
        runSmoothInCurrentJvm(command, additionalArguments);
        break;
        case FULL_BINARY:
          runSmoothInForkedJvm(command);
          break;
      default:
        fail("Unknown mode: " + AcceptanceUtils.TEST_MODE);
    }
  }

  private void runSmoothInCurrentJvm(CommandWithArgs command, String... additionalArgs) {
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    try (PrintWriter outWriter = printWriter(outBytes);
        PrintWriter errWriter = printWriter(errBytes)) {
      String[] commandAndAllArgs = command.commandPlusArgsPlus(additionalArgs);
      exitCode = Main.runSmooth(commandAndAllArgs, outWriter, errWriter);
      outWriter.flush();
      errWriter.flush();
      this.sysOut = outBytes.toString(UTF_8);
      this.sysErr = errBytes.toString(UTF_8);
    }
  }

  private static PrintWriter printWriter(ByteArrayOutputStream outBytes) {
    return new PrintWriter(outBytes, true, UTF_8);
  }

  private void runSmoothInForkedJvm(CommandWithArgs command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(command.commandPlusArgs()));
      processBuilder.directory(projectDirAbsolute());
      Process process = processBuilder.start();
      ExecutorService executor = Executors.newFixedThreadPool(2);
      Future<byte[]> inputStream = executor.submit(() -> toByteArray(process.getInputStream()));
      Future<byte[]> errorStream = executor.submit(() -> toByteArray(process.getErrorStream()));
      exitCode = process.waitFor();
      sysOut = new String(inputStream.get(), UTF_8);
      sysErr = new String(errorStream.get(), UTF_8);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (IOException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public static String[] processArgs(String... params) {
    return concat(SMOOTH_BINARY.toString(), params);
  }

  public void thenFinishedWithSuccess() {
    thenReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void thenFinishedWithError() {
    thenReturnedCode(EXIT_CODE_ERROR);
  }

  private void thenReturnedCode(int expected) {
    if (expected != exitCode) {
      fail("Expected return code " + expected + " but was " + exitCode + ".\n"
          + "standard out:\n" + sysOut + "\n"
          + "standard err:\n" + sysErr + "\n");
    }
  }

  public int exitCode() {
    return exitCode;
  }

  public void thenSysOutContainsParseError(int lineNumber, String... errorLines) {
    errorLines[0] = USER_MODULE_PATH.toString() + ":" + lineNumber + ": " + errorLines[0];
    thenSysOutContainsParseError(errorLines);
  }

  public void thenSysOutContainsParseError(String... errorLines) {
    errorLines[0] = "ERROR: " + errorLines[0];
    thenSysOutContains(
        "  " + USER_MODULE_PATH.toString(),
        prefixMultiline(errorLines));
  }

  public void thenSysOutContains(String... lines) {
    String text = unlines(lines);
    if (!sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut doesn't contain expected substring");
    }
  }

  public void thenSysOutDoesNotContain(String... lines) {
    String text = unlines(lines);
    if (sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut contains forbidden substring");
    }
  }

  public void thenSysErrContains(String... lines) {
    String text = unlines(lines);
    if (!sysErr.contains(text)) {
      failWithFullOutputs(text, "SysErr doesn't contain expected substring");
    }
  }

  private void failWithFullOutputs(String text, String message) {
    // We use isEqualTo() instead of contains() so generated failure text will contain
    // as much data as possible and intellij is able to display it via visual diff.
    assertWithMessage(message)
        .that(unlines(
            "================= SYS-OUT ====================",
            sysOut,
            "================= SYS-ERR ====================",
            sysErr
        ))
        .isEqualTo(text);
  }

  public String sysOut() {
    return sysOut;
  }

  public String sysErr() {
    return sysErr;
  }

  public File artifact(String name) {
    return file(ARTIFACTS_PATH.appendPart(name).toString());
  }

  /**
   * @return given artifact as ByteString, or List<ByteString> if it is array,
   * or List<List<ByteString>> if it is array of depth=2, and so on.
   */
  public Object artifactAsByteStrings(String name) {
    try {
      return actual(artifact(name), BufferedSource::readByteString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean artifactAsBoolean(String name) {
    try {
      return readAndClose(buffer(source(artifact(name))), s -> {
        ByteString value = s.readByteString();
        if (value.size() != 1) {
          throw new RuntimeException("Expected boolean artifact but got " + value.toString());
        }
        switch (value.getByte(0)) {
          case 0:
            return false;
          case 1:
            return true;
          default:
            throw new RuntimeException("Expected boolean artifact but got " + value.toString());
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Object artifactArray(String name) {
    try {
      return actual(artifact(name), s -> s.readString(CHARSET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(File file, DataReader<?> dataReader) throws IOException {
    if (!file.exists()) {
      return null;
    }
    if (file.isDirectory()) {
      return actualArray(file, dataReader);
    }
    return readAndClose(buffer(source(file)), dataReader);
  }

  private static Object actualArray(File file, DataReader<?> dataReader) throws IOException {
    int count = file.list().length;

    List<Object> result = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      result.add(actual(new File(file, Integer.toString(i)), dataReader));
    }
    return result;
  }

  public File file(String path) {
    return new File(projectDirAbsolute(), path);
  }

  /**
   * Absolute path to project dir.
   */
  public File projectDirAbsolute() {
    return projectDir;
  }

  /**
   * Project dir that has been passed via --project-dir option.
   * It may be "." when current dir is equal to {@link #projectDirAbsolute()}.
   */
  public Path projectDirOption() {
    switch (AcceptanceUtils.TEST_MODE) {
      case SINGLE_JVM:
        return projectDir.toPath();
      case FULL_BINARY:
        return Paths.get(".");
      default:
        fail("Unknown mode: " + AcceptanceUtils.TEST_MODE);
        return null;
    }
  }

  public File smoothDir() {
    return new File(projectDirAbsolute(), SMOOTH_DIR.toString());
  }

  public String artifactContent(String artifact) throws IOException {
    File file = artifact(artifact);
    return fileContent(file);
  }

  public Map<String, String> artifactDir(String artifact) throws IOException {
    File dir = artifact(artifact);
    if (!dir.exists()) {
      fail("No such artifact: " + artifact);
    }
    HashMap<String, String> result = new HashMap<>();
    addFilesToMap(dir, "", result);
    return result;
  }

  private void addFilesToMap(File dir, String prefix, HashMap<String, String> result)
      throws IOException {
    for (String fileName : dir.list()) {
      File file = new File(dir, fileName);
      if (file.isDirectory()) {
        addFilesToMap(file, prefix + file.getName() + "/", result);
      } else {
        result.put(prefix + file.getName(), fileContent(file));
      }
    }
  }

  private static String fileContent(File file) throws IOException {
    return readAndClose(buffer(source(file)), s -> s.readString(CHARSET));
  }
}

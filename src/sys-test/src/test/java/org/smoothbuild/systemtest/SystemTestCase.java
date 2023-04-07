package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.util.stream.Collectors.toList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.helpCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.listCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.readAndClose;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.cli.Main;
import org.smoothbuild.util.CommandExecutor;
import org.smoothbuild.util.CommandExecutor.CommandResult;
import org.smoothbuild.util.io.DataReader;

import okio.BufferedSource;
import okio.ByteString;

public abstract class SystemTestCase {
  public static final TestMode TEST_MODE = TestMode.detectTestMode();
  public static final Path SYS_TEST_PROJECT_ROOT = Paths.get(".").toAbsolutePath();
  public static final Path SMOOTH_BINARY =
      Paths.get("./build/installation/smooth/smooth").toAbsolutePath();

  private Path projectDir;
  private Integer exitCode;
  private String sysOut;
  private String sysErr;

  @BeforeEach
  public void init(@TempDir Path projectDir) {
    this.projectDir = projectDir;
  }

  @AfterEach
  public void destroy() throws IOException {
    deleteRecursively(projectDirAbsolutePath());
  }

  public void createUserModule(String code) throws IOException {
    createFile(PRJ_MOD_PATH.toString(), code);
  }

  public void createFile(String path, String content) throws IOException {
    Path fullPath = absolutePath(path);
    createDirectories(fullPath.getParent());
    try (FileWriter writer = new FileWriter(fullPath.toString(), UTF_8)) {
      writer.write(content);
    }
  }

  public void createNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(absolutePath("build.jar"), list(classes));
  }

  public void createDir(String path) throws IOException {
    createDirectories(absolutePath(path));
  }

  public void createJunitLibs() {
    copyLib("junit-4.13.2.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = absolutePath(dirInsideProject);
      createDirectories(destinationDir);
      return Files.copy(
          SYS_TEST_PROJECT_ROOT.resolve("build/junit4files").resolve(jar),
          destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void runSmoothBuild(String... args) {
    runSmooth(buildCommand(args));
  }

  public void runSmoothClean(String... args) {
    runSmooth(cleanCommand(args));
  }

  public void runSmoothHelp(String... args) {
    runSmoothWithoutProjectAndInstallationDir(helpCommand(args));
  }

  public void runSmoothList(String... args) {
    runSmooth(listCommand(args));
  }

  public void runSmoothVersion(String... args) {
    runSmoothWithoutProjectDir(versionCommand(args));
  }

  public void runSmoothWithoutProjectAndInstallationDir(CommandWithArgs command) {
    runSmoothInProperJvm(command);
  }

  public void runSmoothWithoutProjectDir(CommandWithArgs command) {
    runSmoothInProperJvm(command,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  public void runSmooth(CommandWithArgs command) {
    runSmoothInProperJvm(command,
        "--project-dir=" + projectDir,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  private void runSmoothInProperJvm(CommandWithArgs command, String... additionalArgs) {
    switch (TEST_MODE) {
      case SINGLE_JVM -> runSmoothInCurrentJvm(command, additionalArgs);
      case FULL_BINARY -> runSmoothInForkedJvm(command);
      default -> fail("Unknown mode: " + TEST_MODE);
    }
  }

  private void runSmoothInCurrentJvm(CommandWithArgs command, String... args) {
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    try (PrintWriter outWriter = printWriter(outBytes);
        PrintWriter errWriter = printWriter(errBytes)) {
      String[] commandAndAllArgs = command.commandPlusArgsPlus(args);
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
      String[] allArgs = processArgs(command.commandPlusArgs());
      Path workingDir = projectDirAbsolutePath();
      CommandResult r = CommandExecutor.execute(workingDir, allArgs);
      exitCode = r.exitCode();
      sysOut = r.sysOut();
      sysErr = r.sysErr();
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

  public void assertFinishedWithSuccess() {
    assertReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void assertFinishedWithError() {
    assertReturnedCode(EXIT_CODE_ERROR);
  }

  private void assertReturnedCode(int expected) {
    if (expected != exitCode) {
      fail("Expected return code " + expected + " but was " + exitCode + ".\n"
          + "standard out:\n" + sysOut + "\n"
          + "standard err:\n" + sysErr + "\n");
    }
  }

  public int exitCode() {
    return exitCode;
  }

  public void assertSysOutContains(String... lines) {
    String text = unlines(lines);
    if (!sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut doesn't contain expected substring");
    }
  }

  public void assertSysOutDoesNotContain(String... lines) {
    String text = unlines(lines);
    if (sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut contains forbidden substring");
    }
  }

  public void assertSysErrContains(String... lines) {
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

  /**
   * @return given artifact as ByteString, or List<ByteString> if it is array,
   * or List<List<ByteString>> if it is array of depth=2, and so on.
   */
  public Object artifactAsByteStrings(String name) {
    try {
      return actual(artifactAbsolutePath(name), BufferedSource::readByteString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean artifactAsBoolean(String name) {
    try {
      return readAndClose(buffer(source(artifactAbsolutePath(name))), s -> {
        ByteString value = s.readByteString();
        if (value.size() != 1) {
          throw new RuntimeException("Expected boolean artifact but got " + value);
        }
        return switch (value.getByte(0)) {
          case 0 -> false;
          case 1 -> true;
          default -> throw new RuntimeException("Expected boolean artifact but got " + value);
        };
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Integer artifactAsInt(String name) {
    try {
      return readAndClose(buffer(source(artifactAbsolutePath(name))), s -> {
        ByteString value = s.readByteString();
        if (4 < value.size()) {
          throw new RuntimeException("Expected int artifact but got too many bytes: "
              + value.size() + " .");
        }
        return new BigInteger(value.toByteArray()).intValue();
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns content of artifact as string
   * or (when artifact is an array) returns list containing stringified elems.
   * Works with array artifacts of any depth.
   */
  public Object artifactStringified(String name) {
    try {
      return actual(artifactAbsolutePath(name), s -> s.readString(CHARSET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(Path path, DataReader<?> dataReader) throws IOException {
    if (!Files.exists(path)) {
      return null;
    }
    if (Files.isDirectory(path)) {
      return actualArray(path, dataReader);
    }
    return readAndClose(buffer(source(path)), dataReader);
  }

  private static Object actualArray(Path path, DataReader<?> dataReader) throws IOException {
    try (Stream<Path> pathStream = Files.list(path)) {
      long count = pathStream.count();
      List<Object> result = new ArrayList<>((int) count);
      for (int i = 0; i < count; i++) {
        result.add(actual(path.resolve(Integer.toString(i)), dataReader));
      }
      return result;
    }
  }

  /**
   * Project dir that has been passed via --project-dir option.
   * It may be "." when current dir is equal to {@link #projectDirAbsolutePath()}.
   */
  public Path projectDirOption() {
    return switch (TEST_MODE) {
      case SINGLE_JVM -> projectDir;
      case FULL_BINARY -> Path.of(".");
    };
  }

  public String artifactAsString(String artifact) throws IOException {
    return fileContentAsString(artifactAbsolutePath(artifact));
  }

  public Map<String, String> artifactTreeContentAsStrings(String artifact) throws IOException {
    Path dir = artifactAbsolutePath(artifact);
    if (!Files.exists(dir)) {
      fail("No such artifact: " + artifact);
    }
    HashMap<String, String> result = new HashMap<>();
    addFilesToMap(dir, Path.of(""), result);
    return result;
  }

  private static void addFilesToMap(Path rootDir, Path relativePath, HashMap<String, String> result)
      throws IOException {
    try (Stream<Path> stream = Files.list(rootDir.resolve(relativePath))) {
      for (Path path : stream.collect(toList())) {
        Path relative = relativePath.resolve(path.getFileName());
        if (Files.isDirectory(path)) {
          addFilesToMap(rootDir, relative, result);
        } else {
          result.put(relative.toString(), fileContentAsString(path));
        }
      }
    }
  }

  private static String fileContentAsString(Path path) throws IOException {
    return readAndClose(buffer(source(path)), s -> s.readString(CHARSET));
  }

  public ByteString artifactAsByteString(String artifact) throws IOException {
    return fileContentAsByteSTring(artifactAbsolutePath(artifact));
  }

  public static ByteString fileContentAsByteSTring(Path path) throws IOException {
    return readAndClose(buffer(source(path)), BufferedSource::readByteString);
  }

  public Path smoothDirAbsolutePath() {
    return absolutePath(SMOOTH_DIR.toString());
  }

  public Path absolutePath(String path) {
    return projectDirAbsolutePath().resolve(path);
  }

  public Path artifactAbsolutePath(String name) {
    return absolutePath(ARTIFACTS_PATH.appendPart(name).toString());
  }

  /**
   * Absolute path to project dir.
   */
  public Path projectDirAbsolutePath() {
    return projectDir;
  }
}

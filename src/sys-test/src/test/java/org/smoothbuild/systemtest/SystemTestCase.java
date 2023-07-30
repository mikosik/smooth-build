package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.util.Locale.ROOT;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.Strings.unlines;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.common.io.Okios.readAndClose;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.fs.project.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.helpCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.listCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import java.io.FileWriter;
import java.io.IOException;
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
import org.smoothbuild.common.CommandExecutor;
import org.smoothbuild.common.CommandExecutor.CommandResult;
import org.smoothbuild.common.io.DataReader;

import com.google.common.base.Splitter;

import okio.BufferedSource;
import okio.ByteString;

public abstract class SystemTestCase {
  public static final Path SYS_TEST_PROJECT_ROOT = Paths.get(".").toAbsolutePath();
  public static final Path SMOOTH_BINARY = findSmoothBinary();

  private static Path findSmoothBinary() {
    var osName = System.getProperty("os.name").toUpperCase(ROOT);
    if (osName.startsWith("LINUX") || osName.startsWith("MAC OS")) {
      return Paths.get("./build/installation/smooth/bin/smooth").toAbsolutePath();
    } else {
      return Paths.get("./build/installation/smooth/bin/smooth.bat").toAbsolutePath();
    }
  }

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
    runSmoothInForkedJvm(command);
  }

  public void runSmoothWithoutProjectDir(CommandWithArgs command) {
    runSmoothInForkedJvm(command);
  }

  public void runSmooth(CommandWithArgs command) {
    runSmoothInForkedJvm(command);
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

  public void assertSysOutContains(String text) {
    assertWithFullOutputs(sysOut, text, "SysOut");
  }

  public void assertSysErrContains(String text) {
    assertWithFullOutputs(sysErr, text, "SysErr");
  }

  private void assertWithFullOutputs(String out, String text, String outName) {
    var sysOut = this.sysOut;
    var sysErr = this.sysErr;

    var osSpecific = toOsSpecificLineSeparators(text);
    if (!out.contains(osSpecific)) {
      assertWithMessage(unlines(
          outName + " doesn't contain expected substring.",
          "================= SYS-OUT START ====================",
          sysOut,
          "================= SYS-OUT END   ====================",
          "================= SYS-ERR START ====================",
          sysErr,
          "================= SYS-ERR END   ===================="
      )).that(out)
          .isEqualTo(osSpecific);
    }
  }

  public void assertSysOutDoesNotContain(String text) {
    assertWithMessage(unlines(
        "SysOut contains forbidden substring",
        "================= SYS-OUT START ====================",
        sysOut,
        "================= SYS-OUT END   ====================",
        "================= SYS-ERR START ====================",
        sysErr,
        "================= SYS-ERR END   ===================="
    )).that(sysOut)
        .doesNotContain(toOsSpecificLineSeparators(text));
  }

  private static String toOsSpecificLineSeparators(String textBlock) {
    return String.join(System.lineSeparator(), Splitter.on('\n').split(textBlock));
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
      for (Path path : stream.toList()) {
        Path relative = relativePath.resolve(path.getFileName());
        if (Files.isDirectory(path)) {
          addFilesToMap(rootDir, relative, result);
        } else {
          result.put(relative.toString().replace('\\', '/'), fileContentAsString(path));
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

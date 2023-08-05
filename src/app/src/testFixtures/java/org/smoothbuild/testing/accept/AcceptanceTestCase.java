package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.io.Okios.writeAndClose;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.fs.install.InstallationPaths.STD_LIB_MODS;
import static org.smoothbuild.fs.install.InstallationPaths.STD_LIB_MOD_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.DEFAULT_MODULE_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.fs.space.Space.PROJECT;
import static org.smoothbuild.fs.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.fs.space.SpaceUtils.forSpace;
import static org.smoothbuild.out.log.Level.ERROR;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.PathS;
import org.smoothbuild.common.fs.mem.MemoryFileSystemModule;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.fs.install.StandardLibraryFileSystemModule;
import org.smoothbuild.fs.project.ProjectFileSystemModule;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.run.BuildRunner;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import okio.BufferedSink;

public class AcceptanceTestCase extends TestContext {
  private FileSystem stdLibFileSystem;
  private FileSystem prjFileSystem;
  private MemoryReporter memoryReporter;
  private Injector injector;
  private Optional<ImmutableMap<NamedValueS, ValueB>> artifacts;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.memoryReporter = new MemoryReporter();
    this.injector = createInjector(memoryReporter, new MemoryFileSystemModule());

    this.prjFileSystem = injector.getInstance(Key.get(FileSystem.class, forSpace(PROJECT)));
    this.stdLibFileSystem =
        injector.getInstance(Key.get(FileSystem.class, forSpace(STANDARD_LIBRARY)));
    createEmptyStdLibModules(stdLibFileSystem);
  }

  public void createApiNativeJar(Class<?>... classes) throws IOException {
    createJar(stdLibFileSystem.sink(STD_LIB_MOD_PATH.changeExtension("jar")), classes);
  }

  public void createUserNativeJar(Class<?>... classes) throws IOException {
    createJar(prjFileSystem.sink(DEFAULT_MODULE_PATH.changeExtension("jar")), classes);
  }

  private void createJar(BufferedSink fileSink, Class<?>... classes) throws IOException {
    try (var sink = fileSink) {
      saveBytecodeInJar(sink, list(classes));
    }
  }

  protected void createUserModule(String code) throws IOException {
    writeFile(prjFileSystem(), DEFAULT_MODULE_PATH, code);
  }

  protected void evaluate(String... exprs) {
    var buildRunner = injector.getInstance(BuildRunner.class);
    this.artifacts = buildRunner.evaluate(list(exprs));
  }

  protected void resetState() {
    resetDiskData();
    resetMemory();
  }

  protected void resetMemory() {
    memoryReporter = new MemoryReporter();
    var fixedFileSystemModule = new FixedFileSystemModule(prjFileSystem, stdLibFileSystem);
    injector = createInjector(memoryReporter, fixedFileSystemModule);
    artifacts = null;
  }

  protected void resetDiskData() {
    try {
      prjFileSystem.delete(ARTIFACTS_PATH);
      prjFileSystem.delete(TEMPORARY_PATH);
      prjFileSystem.delete(COMPUTATION_CACHE_PATH);
      prjFileSystem.delete(HASHED_DB_PATH);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected ValueB artifact() {
    var artifactsMap = artifactsMap();
    int size = artifactsMap.size();
    return switch (size) {
      case 0 -> fail("Expected artifact but evaluate returned empty list of artifacts.");
      case 1 -> artifactsMap.values().iterator().next();
      default -> fail("Expected single artifact but evaluate returned " + size + " artifacts.");
    };
  }

  protected ValueB artifact(int index) {
    checkArgument(0 <= index);
    var artifactsMap = artifactsMap();
    int size = artifactsMap.size();
    if (size <= index) {
      fail("Expected at least " + index + " artifacts but evaluation returned only " + size + ".");
    }
    return newArrayList(artifactsMap.values()).get(index);
  }

  private Map<NamedValueS, ValueB> artifactsMap() {
    if (artifacts == null) {
      throw new IllegalStateException("Cannot verify any artifact before you execute build.");
    }
    if (memoryReporter.containsAtLeast(ERROR)) {
      fail("Expected artifact but problems have been reported:\n" + memoryReporter.logs());
    }
    if (artifacts.isEmpty()) {
      fail("Expected artifact but evaluate() return null.");
    }
    return artifacts.get();
  }

  protected ImmutableList<Log> logs() {
    return memoryReporter.logs().toList();
  }

  protected void assertLogsContainProblem() {
    assertThat(memoryReporter.logs().containsAtLeast(ERROR))
        .isTrue();
  }

  private FileSystem prjFileSystem() {
    return prjFileSystem;
  }

  private void createEmptyStdLibModules(FileSystem stdLibFileSystem) throws IOException {
    // STD_LIB_MODS has hardcoded list of standard library modules which are loaded
    // upon startup. Until modules are detected automatically we have to provide here
    // at least empty files.
    for (var filePath : STD_LIB_MODS) {
      PathS path = filePath.path();
      writeFile(stdLibFileSystem, path, "");
    }
  }

  private static void writeFile(FileSystem fileSystem, PathS path, String content)
      throws IOException {
    writeAndClose(fileSystem.sink(path), s -> s.writeUtf8(content));
  }

  public static Injector createInjector(MemoryReporter memoryReporter, Module module) {
    return Guice.createInjector(PRODUCTION,
        new TestModule(memoryReporter),
        new ProjectFileSystemModule(),
        new StandardLibraryFileSystemModule(),
        module,
        new BytecodeModule());
  }
}

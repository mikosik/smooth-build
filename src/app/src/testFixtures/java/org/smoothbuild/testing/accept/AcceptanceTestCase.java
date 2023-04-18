package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.fs.space.Space.STD_LIB;
import static org.smoothbuild.install.InstallationPaths.STD_LIB_MODS;
import static org.smoothbuild.install.InstallationPaths.STD_LIB_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.writeAndClose;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.compile.fs.lang.define.NamedValueS;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.SynchronizedFileSystem;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.fs.space.Space;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Console;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.BuildRunner;
import org.smoothbuild.run.eval.EvaluatorBFactory;
import org.smoothbuild.run.eval.EvaluatorBFactoryImpl;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.SandboxHash;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import okio.BufferedSink;

public class AcceptanceTestCase extends TestContext {
  private FileSystem stdLibFileSystem;
  private FileSystem prjFileSystem;
  private MemoryReporter memoryReporter;
  private Injector injector;
  private Optional<ImmutableMap<NamedValueS, ValueB>> artifacts;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.stdLibFileSystem = synchronizedMemoryFileSystem();
    createEmptyStdLibModules(stdLibFileSystem);
    this.prjFileSystem = synchronizedMemoryFileSystem();
    this.memoryReporter = new MemoryReporter();
    this.injector = createInjector(stdLibFileSystem, prjFileSystem, memoryReporter);
  }

  public void createApiNativeJar(Class<?>... classes) throws IOException {
    createJar(stdLibFileSystem.sink(STD_LIB_MOD_PATH.changeExtension("jar")), classes);
  }

  public void createUserNativeJar(Class<?>... classes) throws IOException {
    createJar(prjFileSystem.sink(PRJ_MOD_PATH.changeExtension("jar")), classes);
  }

  private void createJar(BufferedSink fileSink, Class<?>... classes) throws IOException {
    try (var sink = fileSink) {
      saveBytecodeInJar(sink, list(classes));
    }
  }

  protected void createUserModule(String code) throws IOException {
    writeFile(prjFileSystem(), PRJ_MOD_PATH, code);
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
    injector = createInjector(stdLibFileSystem, prjFileSystem, memoryReporter);
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

  public static Injector createInjector(
      FileSystem stdLibFileSystem, FileSystem prjFileSystem, MemoryReporter memoryReporter) {
    return Guice.createInjector(PRODUCTION,
        new TestModule(stdLibFileSystem, prjFileSystem, memoryReporter),
        new BytecodeModule());
  }

  public static class TestModule extends AbstractModule {
    private final FileSystem stdLibFileSystem;
    private final FileSystem prjFileSystem;
    private final MemoryReporter memoryReporter;

    public TestModule(
        FileSystem stdLibFileSystem, FileSystem prjFileSystem, MemoryReporter memoryReporter) {
      this.stdLibFileSystem = stdLibFileSystem;
      this.prjFileSystem = prjFileSystem;
      this.memoryReporter = memoryReporter;
    }

    @Override
    protected void configure() {
      bind(MemoryReporter.class).toInstance(memoryReporter);
      bind(Reporter.class).to(MemoryReporter.class);
      bind(Console.class).toInstance(new Console(new PrintWriter(nullOutputStream(), true)));
      bind(EvaluatorBFactory.class).to(EvaluatorBFactoryImpl.class);
      bind(TaskMatcher.class).toInstance(ALL);
    }

    @Provides
    @Singleton
    public ImmutableMap<Space, FileSystem> provideFileSystems() {
      return ImmutableMap.of(STD_LIB, stdLibFileSystem, PRJ, prjFileSystem);
    }

    private static SynchronizedFileSystem newFileSystem(Path path) {
      return synchronizedMemoryFileSystem();
    }

    @Provides
    @Singleton
    @ForSpace(PRJ)
    public FileSystem providePrjFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
      return fileSystems.get(PRJ);
    }

    @Provides
    @Singleton
    @ForSpace(STD_LIB)
    public FileSystem provideStdLibFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
      return fileSystems.get(STD_LIB);
    }

    @Provides
    @Singleton
    public ImmutableMap<Space, Path> provideSpaceToPathMap() {
      return ImmutableMap.of();
    }

    @Provides
    @Singleton
    @SandboxHash
    public Hash provideSandboxHash() {
      return Hash.of(33);
    }
  }
}

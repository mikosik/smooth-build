package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.ByteStreams.nullOutputStream;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.install.InstallationPaths.API_MOD_PATH;
import static org.smoothbuild.install.InstallationPaths.SDK_MODS;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.PRJ_MOD_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.io.fs.space.Space.SDK;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.writeAndClose;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.bytecode.BytecodeModule;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.eval.SandboxHash;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.io.fs.base.SynchronizedFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.io.fs.space.Space;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.out.console.ConsoleModule;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.BuildRunner;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.parallel.TaskReporter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import okio.BufferedSink;

public class AcceptanceTestCase extends TestingContext {
  private MemoryFileSystem sdkFileSystem;
  private MemoryFileSystem prjFileSystem;
  private MemoryReporter memoryReporter;
  private Injector injector;
  private Optional<Map<TopRefS, ValB>> artifacts;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.sdkFileSystem = new MemoryFileSystem();
    createEmptyApiModules(sdkFileSystem);
    this.prjFileSystem = new MemoryFileSystem();
    this.memoryReporter = new MemoryReporter();
    this.injector = createInjector(sdkFileSystem, prjFileSystem, memoryReporter);
  }

  public void createApiNativeJar(Class<?>... classes) throws IOException {
    createJar(sdkFileSystem.sink(API_MOD_PATH.changeExtension("jar")), classes);
  }

  public void createUserNativeJar(Class<?>... classes) throws IOException {
    createJar(prjFileSystem.sink(PRJ_MOD_PATH.changeExtension("jar")), classes);
  }

  private void createJar(BufferedSink fileSink, Class<?>[] classes) throws IOException {
    try (var sink = fileSink) {
      saveBytecodeInJar(sink, classes);
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
    injector = createInjector(sdkFileSystem, prjFileSystem, memoryReporter);
    artifacts = null;
  }

  protected void resetDiskData() {
    prjFileSystem.delete(ARTIFACTS_PATH);
    prjFileSystem.delete(TEMPORARY_PATH);
    prjFileSystem.delete(COMPUTATION_CACHE_PATH);
    prjFileSystem.delete(HASHED_DB_PATH);
  }

  protected ValB artifact() {
    var artifactsMap = artifactsMap();
    int size = artifactsMap.size();
    return switch (size) {
      case 0 -> fail("Expected artifact but evaluate returned empty list of artifacts.");
      case 1 -> artifactsMap.values().iterator().next();
      default -> fail("Expected single artifact but evaluate returned " + size + " artifacts.");
    };
  }

  protected ValB artifact(int index) {
    checkArgument(0 <= index);
    var artifactsMap = artifactsMap();
    int size = artifactsMap.size();
    if (size <= index) {
      fail("Expected at least " + index + " artifacts but evaluation returned only " + size + ".");
    }
    return newArrayList(artifactsMap.values()).get(index);
  }

  private Map<TopRefS, ValB> artifactsMap() {
    if (artifacts == null) {
      throw new IllegalStateException("Cannot verify any artifact before you execute build.");
    }
    if (memoryReporter.containsProblems()) {
      fail("Expected artifact but problems have been reported:\n" + memoryReporter.logs());
    }
    if (artifacts.isEmpty()) {
      fail("Expected artifact but evaluate() return null.");
    }
    var artifactsMap = artifacts.get();
    return artifactsMap;
  }

  protected ImmutableList<Log> logs() {
    return memoryReporter.logs().toList();
  }

  protected void assertLogsContainProblem() {
    assertThat(memoryReporter.logs().containsProblem())
        .isTrue();
  }

  private FileSystem prjFileSystem() {
    return prjFileSystem;
  }

  private void createEmptyApiModules(MemoryFileSystem sdkFileSystem) throws IOException {
    // SDK_MODULES has hardcoded list of standard library modules which are loaded
    // upon startup. Until modules are detected automatically we have to provide here
    // at least empty files.
    for (var filePath : SDK_MODS) {
      PathS path = filePath.path();
      writeFile(sdkFileSystem, path, "");
    }
  }

  private static void writeFile(FileSystem fileSystem, PathS path, String content)
      throws IOException {
    writeAndClose(fileSystem.sink(path), s -> s.writeUtf8(content));
  }

  public static Injector createInjector(FileSystem sdkFileSystem, FileSystem prjFileSystem,
      MemoryReporter memoryReporter) {
    return Guice.createInjector(PRODUCTION,
        new TestModule(sdkFileSystem, prjFileSystem, memoryReporter),
        new BytecodeModule(),
        new ConsoleModule(new PrintWriter(nullOutputStream(), true)));
  }

  public static class TestModule extends AbstractModule {
    private final FileSystem sdkFileSystem;
    private final FileSystem prjFileSystem;
    private final MemoryReporter memoryReporter;

    public TestModule(FileSystem sdkFileSystem, FileSystem prjFileSystem,
        MemoryReporter memoryReporter) {
      this.sdkFileSystem = sdkFileSystem;
      this.prjFileSystem = prjFileSystem;
      this.memoryReporter = memoryReporter;
    }

    @Override
    protected void configure() {
      bind(MemoryReporter.class).toInstance(memoryReporter);
      bind(Reporter.class).to(MemoryReporter.class);
      bind(TaskReporter.class).to(MemoryReporter.class);
    }

    @Provides
    @Singleton
    public ImmutableMap<Space, FileSystem> provideFileSystems() {
      return ImmutableMap.of(SDK, sdkFileSystem, PRJ, prjFileSystem);
    }

    private static SynchronizedFileSystem newFileSystem(Path path) {
      return new SynchronizedFileSystem(new MemoryFileSystem());
    }

    @Provides
    @Singleton
    @ForSpace(PRJ)
    public FileSystem providePrjFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
      return fileSystems.get(PRJ);
    }

    @Provides
    @Singleton
    @ForSpace(SDK)
    public FileSystem provideSdkFileSystem(ImmutableMap<Space, FileSystem> fileSystems) {
      return fileSystems.get(SDK);
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

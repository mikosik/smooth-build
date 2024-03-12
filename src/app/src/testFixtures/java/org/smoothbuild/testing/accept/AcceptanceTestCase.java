package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.app.run.EvaluateStep.evaluateStep;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.ALL;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.base.Log.containsAnyFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.testing.TestingFileSystem.writeFile;
import static org.smoothbuild.common.testing.TestingSpace.space;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.common.testing.MemoryReporter;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.testing.TestingBytecode;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VirtualMachineModule;

public class AcceptanceTestCase extends TestingBytecode {
  private static final Space MODULES_SPACE = space("module-space");
  private static final Path LIB_MODULE_PATH = path("libraryModule.smooth");
  private static final FullPath LIB_MODULE_FULL_PATH =
      FullPath.fullPath(MODULES_SPACE, LIB_MODULE_PATH);
  private static final Path USER_MODULE_PATH = path("userModule.smooth");
  private static final FullPath USER_MODULE_FULL_PATH = fullPath(MODULES_SPACE, USER_MODULE_PATH);
  private FileSystem projectFileSystem;
  private FileSystem bytecodeDbFileSystem;
  private FileSystem modulesFileSystem;
  private FileSystem computationCacheFileSystem;
  private List<FullPath> modules;
  private Injector injector;
  private Maybe<List<Tuple2<ExprS, ValueB>>> artifacts;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.projectFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    this.modulesFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    this.bytecodeDbFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    this.computationCacheFileSystem = new SynchronizedFileSystem(new MemoryFileSystem());
    this.modules = list();
    this.injector = createInjector();
  }

  protected void createLibraryModule(java.nio.file.Path code, java.nio.file.Path jar)
      throws IOException {
    try (var sink = modulesFileSystem.sink(LIB_MODULE_PATH.changeExtension("jar"))) {
      try (var source = buffer(source(jar))) {
        sink.writeAll(source);
      }
    }
    try (var source = buffer(source(code))) {
      writeFile(modulesFileSystem, LIB_MODULE_PATH, source.readUtf8());
    }
    modules = modules.append(LIB_MODULE_FULL_PATH);
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    if (classes.length != 0) {
      try (var sink = modulesFileSystem.sink(USER_MODULE_PATH.changeExtension("jar"))) {
        saveBytecodeInJar(sink, list(classes));
      }
    }
    writeFile(modulesFileSystem, USER_MODULE_PATH, code);
    modules = modules.append(USER_MODULE_FULL_PATH);
  }

  protected void createProjectFile(String path, String content) throws IOException {
    writeFile(projectFileSystem, path(path), content);
  }

  protected FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  protected void evaluate(String... names) {
    var steps = evaluateStep(modules, listOfAll(asList(names)));
    var reporter = injector.getInstance(Reporter.class);
    this.artifacts = injector.getInstance(StepExecutor.class).execute(steps, null, reporter);
  }

  protected void restartSmoothWithSameFileSystems() {
    injector = createInjector();
    artifacts = null;
  }

  protected ValueB artifact() {
    var artifactsArray = artifactsArray();
    int size = artifactsArray.size();
    return switch (size) {
      case 0 -> fail("Expected artifact but evaluate returned empty list of artifacts.");
      case 1 -> artifactsArray.get(0).element2();
      default -> fail("Expected single artifact but evaluate returned " + size + " artifacts.");
    };
  }

  protected ValueB artifact(int index) {
    checkArgument(0 <= index);
    var artifactsArray = artifactsArray();
    int size = artifactsArray.size();
    if (size <= index) {
      fail("Expected at least " + (index + 1) + " artifacts but evaluation returned only " + size
          + ".");
    }
    return artifactsArray.get(index).element2();
  }

  private List<Tuple2<ExprS, ValueB>> artifactsArray() {
    if (artifacts == null) {
      throw new IllegalStateException("Cannot verify any artifact before you execute build.");
    }
    if (memoryReporter().containsFailure()) {
      fail("Expected artifact but problems have been reported:\n"
          + memoryReporter().logs());
    }
    if (artifacts.isNone()) {
      fail("Expected artifact but evaluate() returned null.");
    }
    return artifacts.get();
  }

  protected List<Log> logs() {
    return memoryReporter().logs();
  }

  protected void assertLogsContainFailure() {
    assertThat(containsAnyFailure(memoryReporter().logs())).isTrue();
  }

  private MemoryReporter memoryReporter() {
    return injector.getInstance(MemoryReporter.class);
  }

  private Injector createInjector() {
    return Guice.createInjector(PRODUCTION, new TestModule(), new VirtualMachineModule());
  }

  @Override
  public CategoryDb categoryDb() {
    return injector.getInstance(CategoryDb.class);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    return injector.getInstance(BytecodeFactory.class);
  }

  public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(MemoryReporter.class).toInstance(new MemoryReporter());
      bind(Reporter.class).to(MemoryReporter.class);
      bind(ReportMatcher.class).toInstance(ALL);
    }

    @Provides
    @Singleton
    @Sandbox
    public Hash provideSandboxHash() {
      return Hash.of(33);
    }

    @Provides
    public Map<Space, FileSystem> provideFileSystemsMap() {
      return map(MODULES_SPACE, modulesFileSystem);
    }

    @Provides
    @ComputationDb
    public FileSystem provideComputationCacheFileSystem() {
      return projectFileSystem;
    }

    @Provides
    @BytecodeDb
    public FileSystem provideBytecodeDbFileSystem() {
      return bytecodeDbFileSystem;
    }

    @Provides
    @Project
    public FileSystem provideProjectFileSystem() {
      return projectFileSystem;
    }
  }

  public Log userFatal(int line, String message) {
    return fatal(userFileMessage(line, message));
  }

  public Log userError(int line, String message) {
    return error(userFileMessage(line, message));
  }

  private String userFileMessage(int line, String message) {
    return USER_MODULE_FULL_PATH + ":" + line + ": " + message;
  }
}

package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.Log.containsAnyFailure;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.testing.TestingFileSystem.writeFile;
import static org.smoothbuild.common.testing.TestingSpace.space;
import static org.smoothbuild.run.EvaluateStep.evaluateStep;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import okio.BufferedSink;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VirtualMachineModule;

public class AcceptanceTestCase extends TestingVirtualMachine {
  private static final Space MODULES_SPACE = space("module-space");
  private static final Path USER_MODULE_PATH = path("userModule.smooth");
  private static final FullPath USER_MODULE_FULL_PATH = fullPath(MODULES_SPACE, USER_MODULE_PATH);
  private MemoryFileSystem projectFileSystem;
  private MemoryFileSystem bytecodeDbFileSystem;
  private MemoryFileSystem modulesFileSystem;
  private MemoryFileSystem computationCacheFileSystem;
  private List<FullPath> modules;
  private Injector injector;
  private Maybe<List<Tuple2<ExprS, ValueB>>> artifacts;

  @BeforeEach
  public void beforeEach() {
    this.projectFileSystem = new MemoryFileSystem();
    this.modulesFileSystem = new MemoryFileSystem();
    this.bytecodeDbFileSystem = new MemoryFileSystem();
    this.computationCacheFileSystem = new MemoryFileSystem();
    this.modules = list();
    this.injector = createInjector();
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    if (classes.length != 0) {
      createJar(modulesFileSystem.sink(USER_MODULE_PATH.changeExtension("jar")), classes);
    }
    writeFile(modulesFileSystem, USER_MODULE_PATH, code);
    modules = modules.append(USER_MODULE_FULL_PATH);
  }

  private void createJar(BufferedSink fileSink, Class<?>... classes) throws IOException {
    try (var sink = fileSink) {
      saveBytecodeInJar(sink, list(classes));
    }
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

  public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(MemoryReporter.class).toInstance(new MemoryReporter());
      bind(Reporter.class).to(MemoryReporter.class);
      bind(TaskMatcher.class).toInstance(ALL);
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

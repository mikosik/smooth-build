package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.compile.frontend.FrontendCompilerStep.frontendCompilerStep;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_MODS;
import static org.smoothbuild.filesystem.install.InstallationLayout.STD_LIB_MOD_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.filesystem.space.SpaceUtils.forSpace;
import static org.smoothbuild.out.log.Log.containsAnyFailure;
import static org.smoothbuild.run.step.Step.stepFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import java.io.IOException;
import okio.BufferedSink;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.filesystem.install.StandardLibrarySpaceModule;
import org.smoothbuild.filesystem.project.ProjectSpaceModule;
import org.smoothbuild.filesystem.space.MemoryFileSystemModule;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.EvaluateStepFactory;
import org.smoothbuild.run.step.StepExecutor;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class AcceptanceTestCase extends TestContext {
  private FileSystem stdLibFileSystem;
  private FileSystem prjFileSystem;
  private MemoryReporter memoryReporter;
  private Injector injector;
  private Maybe<List<Tuple2<ExprS, ValueB>>> artifacts;

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

  protected void evaluate(String... names) {
    var steps =
        frontendCompilerStep().append(list(names)).then(stepFactory(new EvaluateStepFactory()));
    var reporter = injector.getInstance(Reporter.class);
    this.artifacts = injector.getInstance(StepExecutor.class).execute(steps, null, reporter);
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
      prjFileSystem.delete(COMPUTATION_CACHE_PATH);
      prjFileSystem.delete(HASHED_DB_PATH);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
    if (memoryReporter.containsFailure()) {
      fail("Expected artifact but problems have been reported:\n" + memoryReporter.logs());
    }
    if (artifacts.isNone()) {
      fail("Expected artifact but evaluate() returned null.");
    }
    return artifacts.get();
  }

  protected List<Log> logs() {
    return memoryReporter.logs();
  }

  protected void assertLogsContainFailure() {
    assertThat(containsAnyFailure(memoryReporter.logs())).isTrue();
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

  public static Injector createInjector(MemoryReporter memoryReporter, Module module) {
    return Guice.createInjector(
        PRODUCTION,
        new TestModule(memoryReporter),
        new ProjectSpaceModule(),
        new StandardLibrarySpaceModule(),
        module,
        new BytecodeModule());
  }
}

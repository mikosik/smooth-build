package org.smoothbuild.testing.accept;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Log.containsAnyFailure;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.step.Step.stepFactory;
import static org.smoothbuild.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.layout.Layout.STANDARD_LIBRARY_MODULES;
import static org.smoothbuild.layout.Layout.STANDARD_LIBRARY_MODULE_PATH;
import static org.smoothbuild.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.layout.SmoothSpace.STANDARD_LIBRARY;
import static org.smoothbuild.layout.SpaceUtils.forSpace;
import static org.smoothbuild.run.CreateFrontendCompilerStep.frontendCompilerStep;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.io.IOException;
import okio.BufferedSink;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.filesystem.space.MemoryFileSystemModule;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.step.StepExecutor;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compile.frontend.lang.define.ExprS;
import org.smoothbuild.layout.ProjectSpaceModule;
import org.smoothbuild.layout.SmoothSpace;
import org.smoothbuild.layout.StandardLibrarySpaceModule;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.run.EvaluateStepFactory;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public class AcceptanceTestCase extends TestContext {
  private Injector injector;
  private Maybe<List<Tuple2<ExprS, ValueB>>> artifacts;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.injector = createInjector(new MemoryFileSystemModule());
    createEmptyStdLibModules(stdLibFileSystem());
  }

  public void createApiNativeJar(Class<?>... classes) throws IOException {
    createJar(
        stdLibFileSystem().sink(STANDARD_LIBRARY_MODULE_PATH.changeExtension("jar")), classes);
  }

  public void createUserNativeJar(Class<?>... classes) throws IOException {
    createJar(prjFileSystem().sink(DEFAULT_MODULE_PATH.changeExtension("jar")), classes);
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

  protected void restartSmoothWithSameFileSystems() {
    var fixedFileSystemModule = new FixedFileSystemModule(prjFileSystem(), stdLibFileSystem());
    injector = createInjector(fixedFileSystemModule);
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

  private FileSystem prjFileSystem() {
    return fileSystem(PROJECT);
  }

  private FileSystem stdLibFileSystem() {
    return fileSystem(STANDARD_LIBRARY);
  }

  private FileSystem fileSystem(SmoothSpace space) {
    return injector.getInstance(Key.get(FileSystem.class, forSpace(space)));
  }

  private MemoryReporter memoryReporter() {
    return injector.getInstance(MemoryReporter.class);
  }

  private void createEmptyStdLibModules(FileSystem stdLibFileSystem) throws IOException {
    // STANDARD_LIBRARY_MODULES has hardcoded list of standard library modules which are loaded
    // upon startup. Until modules are detected automatically we have to provide here
    // at least empty files.
    for (var filePath : STANDARD_LIBRARY_MODULES) {
      PathS path = filePath.path();
      writeFile(stdLibFileSystem, path, "");
    }
  }

  public static Injector createInjector(com.google.inject.Module fileSystemModule) {
    return Guice.createInjector(
        PRODUCTION,
        new TestModule(),
        new ProjectSpaceModule(),
        new StandardLibrarySpaceModule(),
        fileSystemModule,
        new BytecodeModule());
  }
}

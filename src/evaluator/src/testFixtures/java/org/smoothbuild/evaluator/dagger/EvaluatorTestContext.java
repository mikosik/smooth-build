package org.smoothbuild.evaluator.dagger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.schedule.Tasks.argument;
import static org.smoothbuild.common.testing.AwaitHelper.await;
import static org.smoothbuild.common.testing.TestingFileSystem.createFile;
import static org.smoothbuild.common.testing.TestingInitializer.runInitializer;
import static org.smoothbuild.virtualmachine.dagger.VmTestModule.PROJECT;

import java.io.IOException;
import okio.Source;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.schedule.RunnableScheduler;
import org.smoothbuild.common.schedule.VirtualThreadRunnableScheduler;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.common.testing.TestingFileSystem;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestApi;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;

public class EvaluatorTestContext implements FrontendCompilerTestApi {
  private EvaluatorTestComponent component;
  private List<FullPath> modules;
  private Maybe<EvaluatedExprs> evaluatedExprs;

  public EvaluatorTestContext() {
    this(new VirtualThreadRunnableScheduler());
  }

  public EvaluatorTestContext(RunnableScheduler runnableScheduler) {
    this.component = newComponent(runnableScheduler, newSynchronizedMemoryFileSystem());
    this.modules = list();
  }

  private static EvaluatorTestComponent newComponent(
      RunnableScheduler runnableScheduler, FileSystem<FullPath> fileSystem) {
    var component = DaggerEvaluatorTestComponent.builder()
        .runnableScheduler(runnableScheduler)
        .fileSystem(fileSystem)
        .build();
    runInitializer(component);
    return component;
  }

  private static SynchronizedFileSystem<FullPath> newSynchronizedMemoryFileSystem() {
    return new SynchronizedFileSystem<>(new MemoryFileSystem(set(PROJECT)));
  }

  @Override
  public EvaluatorTestComponent provide() {
    return component;
  }

  protected void createLibraryModule(java.nio.file.Path code, java.nio.file.Path jar)
      throws IOException {
    var fullPath = provide().projectPath().append("libraryModule.smooth");
    try (var sink = buffer(component.fileSystem().sink(fullPath.withExtension("jar")))) {
      try (var source = source(jar)) {
        sink.writeAll(source);
      }
    }
    try (var source = buffer(source(code))) {
      createFile(component.fileSystem(), fullPath, source.readUtf8());
    }
    modules = modules.add(fullPath);
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    createModule(code, classes, moduleFullPath());
  }

  protected void createLibraryModule(String code, Class<?>... classes) throws IOException {
    createModule(code, classes, provide().projectPath().append("library.smooth"));
  }

  private void createModule(String code, Class<?>[] classes, FullPath fullPath) throws IOException {
    if (classes.length != 0) {
      try (var sink = component.fileSystem().sink(fullPath.withExtension("jar"))) {
        saveBytecodeInJar(sink, list(classes));
      }
    }
    createFile(component.fileSystem(), fullPath, code);
    modules = modules.add(fullPath);
  }

  protected void createProjectFile(String path, String content) throws IOException {
    TestingFileSystem.createFile(provide().projectFileSystem(), path(path), content);
  }

  protected void createProjectFile(Path path, Source content) throws IOException {
    TestingFileSystem.createFile(provide().projectFileSystem(), path, content);
  }

  protected void evaluate(String... names) {
    var scheduler = component.scheduler();
    var scheduleEvaluate = component.scheduleEvaluate();
    var evaluated =
        scheduler.submit(scheduleEvaluate, argument(modules), argument(listOfAll(asList(names))));
    await().until(() -> evaluated.toMaybe().isSome());
    this.evaluatedExprs = evaluated.get();
  }

  protected void restartSmoothWithSameFileSystem() {
    this.component = newComponent(component.runnableScheduler(), component.fileSystem());
    evaluatedExprs = null;
  }

  protected BValue artifact() {
    var bValues = evaluatedExprs().bValues();
    int size = bValues.size();
    return switch (size) {
      case 0 -> fail("Expected artifact but evaluate returned empty list of artifacts.");
      case 1 -> bValues.get(0);
      default -> fail("Expected single artifact but evaluate returned " + size + " artifacts.");
    };
  }

  protected BValue artifact(int index) {
    checkArgument(0 <= index);
    var valueBs = evaluatedExprs().bValues();
    int size = valueBs.size();
    if (size <= index) {
      fail("Expected at least " + (index + 1) + " artifacts but evaluation returned only " + size
          + ".");
    }
    return valueBs.get(index);
  }

  private EvaluatedExprs evaluatedExprs() {
    if (evaluatedExprs == null) {
      throw new IllegalStateException("Cannot verify any artifact before you execute build.");
    }
    if (testReporter().containsFailure()) {
      fail("Expected artifact but problems have been reported:\n"
          + testReporter().logs());
    }
    if (evaluatedExprs.isNone()) {
      fail("Expected artifact but evaluate() returned null.");
    }
    return evaluatedExprs.get();
  }

  protected List<Log> logs() {
    return testReporter().logs();
  }

  protected List<Report> reports() {
    return testReporter().reports();
  }

  protected void assertLogsContainFailure() {
    assertThat(containsFailure(logs())).isTrue();
  }

  private TestReporter testReporter() {
    return provide().reporter();
  }

  public BExprDb exprDb() {
    return provide().exprDb();
  }

  public TestReporter reporter() {
    return provide().reporter();
  }

  public StepEvaluator stepEvaluator() {
    return provide().stepEvaluator();
  }

  public Log userFatal(int line, String message) {
    return fatal(userFileMessage(line, message));
  }

  public Log userError(int line, String message) {
    return error(userFileMessage(line, message));
  }

  private String userFileMessage(int line, String message) {
    return moduleFullPath() + ":" + line + ": " + message;
  }
}

package org.smoothbuild.evaluator.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.testing.TestingFilesystem.createFile;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.io.IOException;
import okio.Source;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.SynchronizedBucket;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.testing.ReportTestWiring;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.common.testing.TestingDirFileSystem;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestApi;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.evaluator.EvaluatorWiring;
import org.smoothbuild.evaluator.ScheduleEvaluate;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.evaluate.compute.StepEvaluator;
import org.smoothbuild.virtualmachine.testing.VmTestWiring;

public class EvaluatorTestContext implements FrontendCompilerTestApi {
  private List<FullPath> modules;
  private Injector injector;
  private Maybe<EvaluatedExprs> evaluatedExprs;
  private FileSystem<FullPath> filesystem;
  private Map<Alias, FileSystem<Path>> buckets;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.modules = list();
    this.buckets = map(PROJECT, new SynchronizedBucket(new MemoryBucket()));
    this.injector = createInjector(buckets);
    this.filesystem = injector.getInstance(Key.get(new TypeLiteral<>() {}));
  }

  protected void createLibraryModule(java.nio.file.Path code, java.nio.file.Path jar)
      throws IOException {
    var fullPath = PROJECT_PATH.append("libraryModule.smooth");
    try (var sink = buffer(filesystem.sink(fullPath.withExtension("jar")))) {
      try (var source = source(jar)) {
        sink.writeAll(source);
      }
    }
    try (var source = buffer(source(code))) {
      createFile(filesystem, fullPath, source.readUtf8());
    }
    modules = modules.append(fullPath);
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    if (classes.length != 0) {
      try (var sink = filesystem.sink(moduleFullPath().withExtension("jar"))) {
        saveBytecodeInJar(sink, list(classes));
      }
    }
    createFile(filesystem, moduleFullPath(), code);
    modules = modules.append(moduleFullPath());
  }

  protected void createProjectFile(String path, String content) throws IOException {
    TestingDirFileSystem.createFile(projectDir(), path(path), content);
  }

  protected void createProjectFile(Path path, Source content) throws IOException {
    TestingDirFileSystem.createFile(projectDir(), path, content);
  }

  protected void evaluate(String... names) {
    var scheduler = injector.getInstance(Scheduler.class);
    var initialize = scheduler.submit(injector.getInstance(Initializer.class));
    var evaluated = scheduler.submit(
        list(initialize),
        ScheduleEvaluate.class,
        argument(modules),
        argument(listOfAll(asList(names))));
    await().until(() -> evaluated.toMaybe().isSome());
    this.evaluatedExprs = evaluated.get();
  }

  protected void restartSmoothWithSameBuckets() {
    injector = createInjector(buckets);
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
    return injector.getInstance(TestReporter.class);
  }

  private static Injector createInjector(Map<Alias, FileSystem<Path>> buckets) {
    return Guice.createInjector(
        PRODUCTION,
        new EvaluatorWiring(),
        new CompilerBackendWiring(),
        new VmTestWiring(buckets),
        new ReportTestWiring());
  }

  @Override
  public BExprDb exprDb() {
    return injector.getInstance(BExprDb.class);
  }

  @Override
  public BKindDb kindDb() {
    return injector.getInstance(BKindDb.class);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    return injector.getInstance(BytecodeFactory.class);
  }

  @Override
  public Scheduler scheduler() {
    return injector.getInstance(Scheduler.class);
  }

  @Override
  public TestReporter reporter() {
    return injector.getInstance(TestReporter.class);
  }

  @Override
  public FileSystem<FullPath> filesystem() {
    return filesystem;
  }

  @Override
  public StepEvaluator stepEvaluator() {
    return injector.getInstance(StepEvaluator.class);
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

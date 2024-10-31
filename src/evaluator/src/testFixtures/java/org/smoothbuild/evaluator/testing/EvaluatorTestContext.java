package org.smoothbuild.evaluator.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.testing.TestingBucketId.PROJECT;
import static org.smoothbuild.common.testing.TestingFilesystem.createFile;
import static org.smoothbuild.common.testing.TestingFullPath.BYTECODE_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.COMPUTATION_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.PROJECT_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import okio.Source;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.DecoratingReporter;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.ReportDecorator;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.SchedulerWiring;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.evaluator.EvaluatorWiring;
import org.smoothbuild.evaluator.ScheduleEvaluate;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class EvaluatorTestContext extends BytecodeTestContext {
  private static final FullPath LIB_MODULE_PATH = fullPath(PROJECT, path("libraryModule.smooth"));
  private static final FullPath USER_MODULE_PATH = fullPath(PROJECT, path("userModule.smooth"));
  private Bucket projectBucket;
  private List<FullPath> modules;
  private Injector injector;
  private Maybe<EvaluatedExprs> evaluatedExprs;
  private Filesystem filesystem;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.projectBucket = new SynchronizedBucket(new MemoryBucket());
    this.modules = list();
    this.injector = createInjector();
    this.filesystem = injector.getInstance(Filesystem.class);
  }

  protected void createLibraryModule(java.nio.file.Path code, java.nio.file.Path jar)
      throws IOException {
    try (var sink = buffer(filesystem.sink(LIB_MODULE_PATH.withExtension("jar")))) {
      try (var source = source(jar)) {
        sink.writeAll(source);
      }
    }
    try (var source = buffer(source(code))) {
      createFile(filesystem, LIB_MODULE_PATH, source.readUtf8());
    }
    modules = modules.append(LIB_MODULE_PATH);
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    if (classes.length != 0) {
      try (var sink = filesystem.sink(USER_MODULE_PATH.withExtension("jar"))) {
        saveBytecodeInJar(sink, list(classes));
      }
    }
    createFile(filesystem, USER_MODULE_PATH, code);
    modules = modules.append(USER_MODULE_PATH);
  }

  protected void createProjectFile(String path, String content) throws IOException {
    createFile(filesystem, fullPath(PROJECT, path(path)), content);
  }

  protected void createProjectFile(Path path, Source content) throws IOException {
    createFile(filesystem, fullPath(PROJECT, path), content);
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
    injector = createInjector();
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

  private Injector createInjector() {
    return Guice.createInjector(
        PRODUCTION,
        new TestWiring(),
        new EvaluatorWiring(),
        new VmWiring(),
        new CompilerBackendWiring(),
        new SchedulerWiring());
  }

  @Override
  public BKindDb kindDb() {
    return injector.getInstance(BKindDb.class);
  }

  @Override
  public BytecodeFactory bytecodeF() {
    return injector.getInstance(BytecodeFactory.class);
  }

  public class TestWiring extends AbstractModule {
    @Override
    protected void configure() {
      bind(TestReporter.class).toInstance(new TestReporter());
      bind(ReportMatcher.class).toInstance((label, logs) -> true);
    }

    @Provides
    @Singleton
    public Reporter provideReporter(
        TestReporter testReporter, java.util.Set<ReportDecorator> decorators) {
      return new DecoratingReporter(testReporter, decorators);
    }

    @Provides
    @Singleton
    @Sandbox
    public Hash provideSandboxHash() {
      return Hash.of(33);
    }

    @Provides
    public Map<BucketId, Bucket> provideBucketsMap() {
      return map(PROJECT, projectBucket);
    }

    @Provides
    @BytecodeDb
    public FullPath provideBytecodeDb() {
      return BYTECODE_DB_PATH;
    }

    @Provides
    @ComputationDb
    public FullPath provideComputationDb() {
      return COMPUTATION_DB_PATH;
    }

    @Provides
    @Project
    public FullPath provideProject() {
      return PROJECT_PATH;
    }
  }

  public Log userFatal(int line, String message) {
    return fatal(userFileMessage(line, message));
  }

  public Log userError(int line, String message) {
    return error(userFileMessage(line, message));
  }

  private String userFileMessage(int line, String message) {
    return userModuleFullPath() + ":" + line + ": " + message;
  }

  protected static FullPath userModuleFullPath() {
    return USER_MODULE_PATH;
  }
}

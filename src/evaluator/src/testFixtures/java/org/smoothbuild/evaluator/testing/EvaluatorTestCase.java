package org.smoothbuild.evaluator.testing;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.truth.Truth.assertThat;
import static com.google.inject.Stage.PRODUCTION;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static okio.Okio.source;
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
import static org.smoothbuild.common.testing.TestingBucket.createFile;
import static org.smoothbuild.common.testing.TestingBucketId.bucketId;
import static org.smoothbuild.evaluator.SmoothEvaluationDag.smoothEvaluationDag;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.InitializingDagEvaluator;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.testing.MemoryReporter;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.testing.TestingBytecode;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VirtualMachineWiring;

public class EvaluatorTestCase extends TestingBytecode {
  private static final BucketId MODULES_BUCKET_ID = bucketId("module-bucket");
  private static final Path LIB_MODULE_PATH = path("libraryModule.smooth");
  private static final FullPath LIB_MODULE_FULL_PATH =
      FullPath.fullPath(MODULES_BUCKET_ID, LIB_MODULE_PATH);
  private static final Path USER_MODULE_PATH = path("userModule.smooth");
  private static final FullPath USER_MODULE_FULL_PATH =
      fullPath(MODULES_BUCKET_ID, USER_MODULE_PATH);
  private Bucket projectBucket;
  private Bucket bytecodeDbBucket;
  private Bucket modulesBucket;
  private Bucket computationCacheBucket;
  private List<FullPath> modules;
  private Injector injector;
  private Maybe<EvaluatedExprs> evaluatedExprs;

  @BeforeEach
  public void beforeEach() throws IOException {
    this.projectBucket = new SynchronizedBucket(new MemoryBucket());
    this.modulesBucket = new SynchronizedBucket(new MemoryBucket());
    this.bytecodeDbBucket = new SynchronizedBucket(new MemoryBucket());
    this.computationCacheBucket = new SynchronizedBucket(new MemoryBucket());
    this.modules = list();
    this.injector = createInjector();
  }

  protected void createLibraryModule(java.nio.file.Path code, java.nio.file.Path jar)
      throws IOException {
    try (var sink = buffer(modulesBucket.sink(LIB_MODULE_PATH.changeExtension("jar")))) {
      try (var source = source(jar)) {
        sink.writeAll(source);
      }
    }
    try (var source = buffer(source(code))) {
      createFile(modulesBucket, LIB_MODULE_PATH, source.readUtf8());
    }
    modules = modules.append(LIB_MODULE_FULL_PATH);
  }

  protected void createUserModule(String code, Class<?>... classes) throws IOException {
    if (classes.length != 0) {
      try (var sink = modulesBucket.sink(USER_MODULE_PATH.changeExtension("jar"))) {
        saveBytecodeInJar(sink, list(classes));
      }
    }
    createFile(modulesBucket, USER_MODULE_PATH, code);
    modules = modules.append(USER_MODULE_FULL_PATH);
  }

  protected void createProjectFile(String path, String content) throws IOException {
    createFile(projectBucket, path(path), content);
  }

  protected Bucket projectBucket() {
    return projectBucket;
  }

  protected void evaluate(String... names) {
    var evaluated = smoothEvaluationDag(modules, listOfAll(asList(names)));
    this.evaluatedExprs = injector.getInstance(InitializingDagEvaluator.class).evaluate(evaluated);
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
    if (memoryReporter().containsFailure()) {
      fail("Expected artifact but problems have been reported:\n"
          + memoryReporter().logs());
    }
    if (evaluatedExprs.isNone()) {
      fail("Expected artifact but evaluate() returned null.");
    }
    return evaluatedExprs.get();
  }

  protected List<Log> logs() {
    return memoryReporter().logs();
  }

  protected void assertLogsContainFailure() {
    assertThat(containsFailure(memoryReporter().logs())).isTrue();
  }

  private MemoryReporter memoryReporter() {
    return injector.getInstance(MemoryReporter.class);
  }

  private Injector createInjector() {
    return Guice.createInjector(PRODUCTION, new TestWiring(), new VirtualMachineWiring());
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
      bind(MemoryReporter.class).toInstance(new MemoryReporter());
      bind(Reporter.class).to(MemoryReporter.class);
      bind(ReportMatcher.class).toInstance((label, logs) -> true);
    }

    @Provides
    @Singleton
    @Sandbox
    public Hash provideSandboxHash() {
      return Hash.of(33);
    }

    @Provides
    public Map<BucketId, Bucket> provideBucketsMap() {
      return map(MODULES_BUCKET_ID, modulesBucket);
    }

    @Provides
    @ComputationDb
    public Bucket provideComputationCacheBucket() {
      return computationCacheBucket;
    }

    @Provides
    @BytecodeDb
    public Bucket provideBytecodeDbBucket() {
      return bytecodeDbBucket;
    }

    @Provides
    @Project
    public Bucket provideProjectBucket() {
      return projectBucket;
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

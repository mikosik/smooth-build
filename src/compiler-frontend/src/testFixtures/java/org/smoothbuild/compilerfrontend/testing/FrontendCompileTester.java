package org.smoothbuild.compilerfrontend.testing;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.inject.Stage.PRODUCTION;
import static org.awaitility.Awaitility.await;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Tasks.argument;
import static org.smoothbuild.common.testing.TestingAlias.LIBRARY;
import static org.smoothbuild.common.testing.TestingAlias.PROJECT;
import static org.smoothbuild.common.testing.TestingBucket.createFile;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.DEFAULT_MODULE_PATH;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.STANDARD_LIBRARY_MODULE_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import java.io.IOException;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketResolver;
import org.smoothbuild.common.bucket.base.FileResolver;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.task.Scheduler;
import org.smoothbuild.common.task.SchedulerWiring;
import org.smoothbuild.common.testing.TestReporter;
import org.smoothbuild.compilerfrontend.FrontendCompile;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;

public class FrontendCompileTester {
  private final String sourceCode;
  private String importedSourceCode;
  private Try<SModule> moduleS;

  public static FrontendCompileTester module(String sourceCode) {
    return new FrontendCompileTester(sourceCode);
  }

  private FrontendCompileTester(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public FrontendCompileTester withImported(String imported) {
    this.importedSourceCode = imported;
    return this;
  }

  public FrontendCompileTester loadsWithSuccess() {
    moduleS = loadModule();
    assertWithMessage(messageWithSourceCode()).that(moduleS.logs()).isEmpty();
    return this;
  }

  public void containsEvaluable(SNamedEvaluable expected) {
    String name = expected.name();
    var actual = assertContainsEvaluable(name);
    assertThat(actual).isEqualTo(expected);
  }

  public void containsEvaluableWithSchema(String name, SchemaS expectedT) {
    var referenceable = assertContainsEvaluable(name);
    assertThat(referenceable.schema()).isEqualTo(expectedT);
  }

  private SNamedEvaluable assertContainsEvaluable(String name) {
    var evaluables = moduleS.get().members().evaluables();
    assertWithMessage("Module doesn't contain '" + name + "'.")
        .that(evaluables.contains(name))
        .isTrue();
    return evaluables.get(name);
  }

  public void containsType(SType expected) {
    var name = expected.name();
    var types = moduleS.get().members().types();
    assertWithMessage("Module doesn't contain value with '" + name + "' type.")
        .that(types.contains(name))
        .isTrue();
    SType actual = types.get(name).type();
    assertWithMessage("Module contains type '" + name + "', but").that(actual).isEqualTo(expected);
  }

  public SModule getLoadedModule() {
    return moduleS.get();
  }

  public void loadsWithProblems() {
    var module = loadModule();
    assertWithMessage(messageWithSourceCode())
        .that(containsFailure(module.logs()))
        .isTrue();
  }

  public void loadsWithError(int line, String message) {
    loadsWith(err(line, message));
  }

  public void loadsWithError(String message) {
    loadsWith(error(message));
  }

  public void loadsWith(Log... logs) {
    var module = loadModule();
    assertWithMessage(messageWithSourceCode()).that(module.logs()).containsExactlyElementsIn(logs);
  }

  private String messageWithSourceCode() {
    return "For source code = "
        + "\n====================\n"
        + sourceCode
        + "\n====================\n";
  }

  private Try<SModule> loadModule() {
    var projectBucket = new SynchronizedBucket(new MemoryBucket());
    var libraryBucket = new SynchronizedBucket(new MemoryBucket());
    Map<Alias, Bucket> buckets = map(PROJECT, projectBucket, LIBRARY, libraryBucket);
    var fileResolver = new FileResolver(new BucketResolver(buckets));
    var testReporter = new TestReporter();

    var injector = Guice.createInjector(PRODUCTION, new AbstractModule() {
      @Override
      protected void configure() {
        bind(Reporter.class).toInstance(testReporter);
        install(new SchedulerWiring());
      }

      @Provides
      public FileResolver provideFileResolver() {
        return fileResolver;
      }
    });
    writeModuleFilesToBuckets(buckets);
    var scheduler = injector.getInstance(Scheduler.class);
    var paths = list(STANDARD_LIBRARY_MODULE_PATH, DEFAULT_MODULE_PATH);
    var module = scheduler.submit(FrontendCompile.class, argument(paths));
    await().until(() -> module.toMaybe().isSome());
    return Try.of(module.get().getOr(null), testReporter.logs());
  }

  private void writeModuleFilesToBuckets(Map<Alias, Bucket> buckets) {
    writeModuleFile(
        buckets,
        STANDARD_LIBRARY_MODULE_PATH,
        importedSourceCode == null ? "" : importedSourceCode);
    writeModuleFile(buckets, DEFAULT_MODULE_PATH, sourceCode);
  }

  private static void writeModuleFile(
      Map<Alias, Bucket> buckets, FullPath fullPath, String content) {
    try {
      createFile(buckets.get(fullPath.alias()), fullPath.path(), content);
    } catch (IOException e) {
      throw new RuntimeException("Can't happen for MemoryBucket.", e);
    }
  }

  public static Log err(int line, String message) {
    return error("{t-project}/build.smooth:" + line + ": " + message);
  }
}

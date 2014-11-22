package org.smoothbuild.builtin.java;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;
import static org.testory.Testory.then;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.builtin.java.junit.err.JunitTestFailedError;
import org.smoothbuild.builtin.java.junit.err.NoJunitTestFoundWarning;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class JunitTest {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESS_TEST_CLASS = "MyClassTest";

  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  Path srcPath = path("src");
  Path fakeJunitPath = path("junit");

  @Test
  public void junit_function_succeeds_when_all_junit_tests_succeed() throws Exception {
    createTestAnnotation();
    createSuccessfulTest();

    script(fileSystem, createscript());

    buildWorker.run(asList("run"));
    then(userConsole.messages().isEmpty());
  }

  @Test
  public void junit_function_fails_when_junit_test_fails() throws Exception {
    createTestAnnotation();
    createFailingTest();

    script(fileSystem, createscript());

    buildWorker.run(asList("run"));
    userConsole.messages().assertContainsOnly(JunitTestFailedError.class);
  }

  @Test
  public void waring_is_logged_when_no_test_is_found() throws Exception {
    createTestAnnotation();
    fileSystem.createDir(srcPath);

    script(fileSystem, createscript());

    buildWorker.run(asList("run"));
    userConsole.messages().assertNoProblems();
    userConsole.messages().assertContains(NoJunitTestFoundWarning.class);
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    createTestAnnotation();
    createSuccessfulTest();
    createFailingTest();

    script(fileSystem, createscript(SUCCESS_TEST_CLASS + ".class"));

    buildWorker.run(asList("run"));
    then(userConsole.messages().isEmpty());
  }

  private String createscript() {
    return createscript(null);
  }

  private String createscript(String pattern) {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("sources: files(" + srcPath + ");");
    builder.addLine("fakeJunitJar: files(" + fakeJunitPath + ") | javac | jar;");
    builder.addLine("jarFile: sources | javac(libs=[fakeJunitJar]) | jar;");
    String include = pattern == null ? "" : ", include='" + pattern + "'";
    builder.addLine("run: junit(libs=[jarFile]" + include + ");");
    return builder.build();
  }

  // Creating fake @Test annotation class is the simplest way to compile junit
  // tests
  private void createTestAnnotation() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("package org.junit;");
    builder.addLine("import java.lang.annotation.ElementType;");
    builder.addLine("import java.lang.annotation.Retention;");
    builder.addLine("import java.lang.annotation.RetentionPolicy;");
    builder.addLine("import java.lang.annotation.Target;");

    builder.addLine("@Retention(RetentionPolicy.RUNTIME)");
    builder.addLine("@Target({ ElementType.METHOD })");
    builder.addLine("public @interface Test {");
    builder.addLine("  static class None extends Throwable {");
    builder.addLine("    private static final long serialVersionUID = 1L;");
    builder.addLine("    private None() {");
    builder.addLine("    }");
    builder.addLine("  }");
    builder.addLine("  Class<? extends Throwable> expected() default None.class;");
    builder.addLine("  long timeout() default 0L;");
    builder.addLine("}");

    String sourceCode = builder.build();

    Path path = fakeJunitPath.append(path("org/junit/Test.java"));
    fileSystem.createFile(path, sourceCode);
  }

  private void createSuccessfulTest() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("public class " + SUCCESS_TEST_CLASS + " {");
    builder.addLine("  @org.junit.Test");
    builder.addLine("  public void testMyMethod() {");
    builder.addLine("  }");
    builder.addLine("}");
    String sourceCode = builder.build();

    Path path = srcPath.append(path("MyClassTest.java"));
    fileSystem.createFile(path, sourceCode);
  }

  private void createFailingTest() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("public class " + FAILING_TEST_CLASS + " {");
    builder.addLine("  @org.junit.Test");
    builder.addLine("  public void testMyMethod() {");
    builder.addLine("    throw new AssertionError();");
    builder.addLine("  }");
    builder.addLine("}");
    String sourceCode = builder.build();

    Path path = srcPath.append(path("MyClassFailingTest.java"));
    fileSystem.createFile(path, sourceCode);
  }
}

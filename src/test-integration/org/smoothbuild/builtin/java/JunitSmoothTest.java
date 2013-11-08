package org.smoothbuild.builtin.java;

import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.smoothbuild.builtin.java.junit.JunitTestFailedError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

// TODO
@Ignore("For strange reasons this test fails when run by ant")
public class JunitSmoothTest extends IntegrationTestCase {
  Path srcPath = path("src");
  Path fakeJunitPath = path("junit");

  @Test
  public void runSuccessfulTest() throws Exception {
    createTestAnnotation();
    createSuccessfulTest();

    script(createScript());

    build("run");
    userConsole.assertNoProblems();
  }

  @Test
  public void runFailingTest() throws Exception {
    createTestAnnotation();
    createFailingTest();

    script(createScript());

    build("run");
    userConsole.assertOnlyProblem(JunitTestFailedError.class);
  }

  private String createScript() {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("sources: files(" + srcPath + ");");
    builder.addLine("fakeJunitJar: files(" + fakeJunitPath + ") | javac | jar;");
    builder.addLine("jarFile: sources | javac(libs=[fakeJunitJar]) | jar;");
    builder.addLine("run: junit(libs=[jarFile]);");
    String script = builder.build();
    return script;
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
    builder.addLine("public class MyClassTest {");
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
    builder.addLine("public class MyClassFailingTest {");
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

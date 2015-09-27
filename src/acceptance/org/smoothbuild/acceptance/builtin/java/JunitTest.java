package org.smoothbuild.acceptance.builtin.java;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JunitTest extends AcceptanceTestCase {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESSFUL_TEST_CLASS = "MyClassTest";

  @Test
  public void junit_function_succeeds_when_all_junit_tests_succeed() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenScript("fakeJunit: files('junit') | javac | jar;\n"
        + "srcJar: files('src') | javac(libs=[fakeJunit]) | jar; result: junit(libs=[srcJar]);");
    whenSmoothBuild("result");
    thenReturnedCode(0);
  }

  @Test
  public void junit_function_fails_when_junit_test_fails() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    givenScript("fakeJunit: files('junit') | javac | jar;\n"
        + "srcJar: files('src') | javac(libs=[fakeJunit]) | jar; result: junit(libs=[srcJar]);");
    whenSmoothBuild("result");
    thenReturnedCode(1);
    then(output(), containsString("test failed"));
  }

  @Test
  public void waring_is_logged_when_no_test_is_found() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenDir("src");
    givenScript("fakeJunit: files('junit') | javac | jar;\n"
        + "srcJar: files('src') | javac(libs=[fakeJunit]) | jar; result: junit(libs=[srcJar]);");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(output(), containsString("No junit tests found."));
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());

    givenScript("fakeJunit: files('junit') | javac | jar;\n"
        + "srcJar: files('src') | javac(libs=[fakeJunit]) | jar;\n"
        + "result: junit(libs=[srcJar], include='" + SUCCESSFUL_TEST_CLASS + ".class');");
    whenSmoothBuild("result");
    thenReturnedCode(0);
  }

  private static String successfulTestSourceCode() {
    StringBuilder builder = new StringBuilder();
    builder.append("public class " + SUCCESSFUL_TEST_CLASS + " {\n");
    builder.append(" @org.junit.Test\n");
    builder.append(" public void testMyMethod() {\n");
    builder.append(" }\n");
    builder.append("}\n");
    return builder.toString();
  }

  private static String testAnnotationSourceCode() {
    StringBuilder builder = new StringBuilder();
    builder.append("package org.junit;\n");
    builder.append("import java.lang.annotation.ElementType;\n");
    builder.append("import java.lang.annotation.Retention;\n");
    builder.append("import java.lang.annotation.RetentionPolicy;\n");
    builder.append("import java.lang.annotation.Target;\n");

    builder.append("@Retention(RetentionPolicy.RUNTIME)\n");
    builder.append("@Target({ ElementType.METHOD })\n");
    builder.append("public @interface Test {\n");
    builder.append("  static class None extends Throwable {\n");
    builder.append("    private static final long serialVersionUID = 1L;\n");
    builder.append("    private None() {\n");
    builder.append("    }\n");
    builder.append("  }\n");
    builder.append("  Class<? extends Throwable> expected() default None.class;\n");
    builder.append("  long timeout() default 0L;\n");
    builder.append("}\n");
    return builder.toString();
  }

  private static String failingTestSourceCode() {
    StringBuilder builder = new StringBuilder();
    builder.append("public class " + FAILING_TEST_CLASS + " {\n");
    builder.append("  @org.junit.Test\n");
    builder.append("  public void testMyMethod() {\n");
    builder.append("    throw new AssertionError();\n");
    builder.append("  }\n");
    builder.append("}\n");
    return builder.toString();
  }
}

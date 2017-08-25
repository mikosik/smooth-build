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
    givenScript("fakeJunit = files('//junit') | javac() | jar();\n"
        + "srcJar = files('//src') | javac(libs=[fakeJunit()]) | jar();"
        + " result = junit(libs=[srcJar()]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void junit_function_fails_when_junit_test_fails() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    givenScript("fakeJunit = files('//junit') | javac() | jar();\n"
        + "srcJar = files('//src') | javac(libs=[fakeJunit()]) | jar();"
        + " result = junit(libs=[srcJar()]);");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("test failed"));
  }

  @Test
  public void warning_is_logged_when_no_test_is_found() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenDir("src");
    givenScript("fakeJunit = files('//junit') | javac() | jar();\n"
        + "srcJar = files('//src') | javac(libs=[fakeJunit()]) | jar();"
        + " result = junit(libs=[srcJar()]);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(output(), containsString("No junit tests found."));
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    givenFile("junit/org/junit/Test.java", testAnnotationSourceCode());
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());

    givenScript("fakeJunit = files('//junit') | javac() | jar();\n"
        + "srcJar = files('//src') | javac(libs=[fakeJunit()]) | jar();\n"
        + "result = junit(libs=[srcJar()], include='" + SUCCESSFUL_TEST_CLASS + ".class');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  private static String successfulTestSourceCode() {
    return "public class " + SUCCESSFUL_TEST_CLASS + " {\n"
        + "  @org.junit.Test\n"
        + "  public void testMyMethod() {\n"
        + "  }\n"
        + "}\n";
  }

  private static String testAnnotationSourceCode() {
    return "package org.junit;\n"
        + "import java.lang.annotation.ElementType;\n"
        + "import java.lang.annotation.Retention;\n"
        + "import java.lang.annotation.RetentionPolicy;\n"
        + "import java.lang.annotation.Target;\n"
        + "\n"
        + "@Retention(RetentionPolicy.RUNTIME)\n"
        + "@Target({ ElementType.METHOD })\n"
        + "public @interface Test {\n"
        + "  static class None extends Throwable {\n"
        + "    private static final long serialVersionUID = 1L;\n"
        + "    private None() {\n"
        + "    }\n"
        + "  }\n"
        + "  Class<? extends Throwable> expected() default None.class;\n"
        + "  long timeout() default 0L;\n"
        + "}\n";
  }

  private static String failingTestSourceCode() {
    return "public class " + FAILING_TEST_CLASS + " {\n"
        + "  @org.junit.Test\n"
        + "  public void testMyMethod() {\n"
        + "    throw new AssertionError();\n"
        + "  }\n"
        + "}\n";
  }
}

package org.smoothbuild.acceptance.builtin.java;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JunitTest extends AcceptanceTestCase {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESSFUL_TEST_CLASS = "MyClassTest";

  @Test
  public void junit_fails_when_deps_doesnt_contain_junit_jar() throws Exception {
    givenJunitCopied();
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenScript(
        "  junitJars = files('//junit') ;                          ",
        "  srcJar = files('//src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=[]);                  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
  }

  @Test
  public void junit_function_succeeds_when_all_junit_tests_succeed() throws Exception {
    givenJunitCopied();
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenScript(
        "  junitJars = files('//junit') ;                          ",
        "  srcJar = files('//src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void junit_function_fails_when_junit_test_fails() throws Exception {
    givenJunitCopied();
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    givenScript(
        "  junitJars = files('//junit') ;                          ",
        "  srcJar = files('//src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("test failed");
  }

  @Test
  public void warning_is_logged_when_no_test_is_found() throws Exception {
    givenJunitCopied();
    givenDir("src");
    givenScript(
        "  junitJars = files('//junit') ;                          ",
        "  srcJar = files('//src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenOutputContains("No junit tests found.");
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    givenJunitCopied();
    givenFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    givenFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    givenScript(
        "  junitJars = files('//junit') ;                           ",
        "  srcJar = files('//src') | javac(libs=junitJars) | jar;   ",
        "  result = junit(include='" + SUCCESSFUL_TEST_CLASS + "',  ",
        "    tests=srcJar, deps=junitJars);                         ");
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

  private static String failingTestSourceCode() {
    return "public class " + FAILING_TEST_CLASS + " {\n"
        + "  @org.junit.Test\n"
        + "  public void testMyMethod() {\n"
        + "    throw new AssertionError();\n"
        + "  }\n"
        + "}\n";
  }
}

package org.smoothbuild.acceptance.slib.java;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class JunitTest extends AcceptanceTestCase {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESSFUL_TEST_CLASS = "MyClassTest";

  @Test
  public void junit_fails_when_deps_doesnt_contain_junit_jar() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule(
        "  junitJars = files('junit') ;                          ",
        "  srcJar = files('src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=[]);                  ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
  }

  @Test
  public void junit_function_succeeds_when_all_junit_tests_succeed() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule(
        "  junitJars = files('junit') ;                          ",
        "  srcJar = files('src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void junit_function_fails_when_junit_test_fails() throws Exception {
    createJunitLibs();
    createFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule(
        "  junitJars = files('junit') ;                          ",
        "  srcJar = files('src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("test failed");
  }

  @Test
  public void warning_is_logged_when_no_test_is_found() throws Exception {
    createJunitLibs();
    createDir("src");
    createUserModule(
        "  junitJars = files('junit') ;                          ",
        "  srcJar = files('src') | javac(libs=junitJars) | jar;  ",
        "  result = junit(tests=srcJar, deps=junitJars);           ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("No junit tests found.");
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule(
        "  junitJars = files('junit') ;                           ",
        "  srcJar = files('src') | javac(libs=junitJars) | jar;   ",
        "  result = junit(include='" + SUCCESSFUL_TEST_CLASS + "',  ",
        "    tests=srcJar, deps=junitJars);                         ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
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

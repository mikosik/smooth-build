package org.smoothbuild.systemtest.slib.java;

import static java.lang.String.format;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class JunitTest extends SystemTestCase {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESSFUL_TEST_CLASS = "MyClassTest";

  @Test
  public void junit_fails_when_deps_doesnt_contain_junit_jar() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule("""
            junitJars = projectFiles("junit");
            srcJar = projectFiles("src") > javac(libs=junitJars) > jar() > file("test.jar");                                  
            result = junit(tests=srcJar, deps=[]);                  
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?");
  }

  @Test
  public void junit_func_succeeds_when_all_junit_tests_succeed() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule("""
            junitJars = projectFiles("junit");
            srcJar = projectFiles("src") > javac(libs=junitJars) > jar() > file("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  @Test
  public void junit_func_fails_when_junit_test_fails() throws Exception {
    createJunitLibs();
    createFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule("""
            junitJars = projectFiles("junit");
            srcJar = projectFiles("src") > javac(libs=junitJars) > jar() > file("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("test failed");
  }

  @Test
  public void warning_is_logged_when_no_test_is_found() throws Exception {
    createJunitLibs();
    createDir("src");
    createUserModule("""
            junitJars = projectFiles("junit");
            srcJar = projectFiles("src") > javac(libs=junitJars) > jar() > file("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertSysOutContains("No junit tests found.");
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    createJunitLibs();
    createFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule(format("""
            junitJars = projectFiles("junit");
            srcJar = projectFiles("src") > javac(libs=junitJars) > jar() > file("src.jar");
            result = junit(include="%s", tests=srcJar, deps=junitJars);
            """, SUCCESSFUL_TEST_CLASS));
    runSmoothBuild("result");
    assertFinishedWithSuccess();
  }

  private static String successfulTestSourceCode() {
    return format("""
        public class %s {
          @org.junit.Test
          public void testMyMethod() {
          }
        }
        """, SUCCESSFUL_TEST_CLASS);
  }

  private static String failingTestSourceCode() {
    return format("""
        public class %s {
          @org.junit.Test
          public void testMyMethod() {
            throw new AssertionError();
          }
        }
        """, FAILING_TEST_CLASS);
  }
}

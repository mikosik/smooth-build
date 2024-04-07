package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.common.testing.TestingBucket.createFile;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class JunitTest extends StandardLibraryTestCase {
  private static final String FAILING_TEST_CLASS = "MyClassFailingTest";
  private static final String SUCCESSFUL_TEST_CLASS = "MyClassTest";

  @Test
  public void junit_fails_when_deps_doesnt_contain_junit_jar() throws Exception {
    createJunitLibs();
    createProjectFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule(
        """
            junitJars = files("junit");
            srcJar = files("src") > javac(libs=junitJars) > jar() > File("test.jar");
            result = junit(tests=srcJar, deps=[]);
            """);
    evaluate("result");
    assertThat(logs())
        .contains(error("Cannot find org.junit.runner.JUnitCore. Is junit.jar added to 'deps'?"));
  }

  @Test
  public void junit_func_succeeds_when_all_junit_tests_succeed() throws Exception {
    createJunitLibs();
    createProjectFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createUserModule(
        """
            junitJars = files("junit");
            srcJar = files("src") > javac(libs=junitJars) > jar() > File("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    evaluate("result");
    assertThat(artifact()).isNotNull();
  }

  @Test
  public void junit_func_fails_when_junit_test_fails() throws Exception {
    createJunitLibs();
    createProjectFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule(
        """
            junitJars = files("junit");
            srcJar = files("src") > javac(libs=junitJars) > jar() > File("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    evaluate("result");
    var filtered =
        logs().filter(l -> l.message().startsWith("test failed: testMyMethod(MyClassFailingTest)"));
    assertThat(filtered).isNotEmpty();
  }

  @Test
  public void warning_is_logged_when_no_test_is_found() throws Exception {
    createJunitLibs();
    createProjectFile("src/empty", "");
    createUserModule(
        """
            junitJars = files("junit");
            srcJar = [] > jar() > File("src.jar");
            result = junit(tests=srcJar, deps=junitJars);
            """);
    evaluate("result");
    assertThat(logs()).contains(warning("No junit tests found."));
  }

  @Test
  public void only_test_matching_pattern_are_executed() throws Exception {
    createJunitLibs();
    createProjectFile("src/" + SUCCESSFUL_TEST_CLASS + ".java", successfulTestSourceCode());
    createProjectFile("src/" + FAILING_TEST_CLASS + ".java", failingTestSourceCode());
    createUserModule(format(
        """
            junitJars = files("junit");
            srcJar = files("src") > javac(libs=junitJars) > jar() > File("src.jar");
            result = junit(include="%s", tests=srcJar, deps=junitJars);
            """,
        SUCCESSFUL_TEST_CLASS));
    evaluate("result");
    assertThat(artifact()).isNotNull();
  }

  private static String successfulTestSourceCode() {
    return format(
        """
        public class %s {
          @org.junit.Test
          public void testMyMethod() {
          }
        }
        """,
        SUCCESSFUL_TEST_CLASS);
  }

  private static String failingTestSourceCode() {
    return format(
        """
        public class %s {
          @org.junit.Test
          public void testMyMethod() {
            throw new AssertionError();
          }
        }
        """,
        FAILING_TEST_CLASS);
  }

  private void createJunitLibs() throws IOException {
    copyLib("junit-4.13.2.jar", "junit");
    copyLib("hamcrest-core-1.3.jar", "junit");
  }

  private void copyLib(String jarFileName, String dirInsideProject) throws IOException {
    var sourcePath = java.nio.file.Path.of("./build/junit4files").resolve(jarFileName);
    try (var source = buffer(source(sourcePath.toAbsolutePath()))) {
      var destinationPath =
          path(dirInsideProject).appendPart(sourcePath.getFileName().toString());
      createFile(projectBucket(), destinationPath, source);
    }
  }
}

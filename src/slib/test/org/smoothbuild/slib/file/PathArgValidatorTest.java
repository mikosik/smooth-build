package org.smoothbuild.slib.file;

import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.plugin.AbortException;
import org.smoothbuild.testing.TestingContext;

public class PathArgValidatorTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("listOfCorrectProjectPaths")
  public void valid_project_paths_are_accepted(String path) {
    validatedProjectPath(container(), "name", string(path));
  }

  public static Stream<String> listOfCorrectProjectPaths() {
    return Stream.of(
        ".",

        "abc",
        "abc/def",
        "abc/def/ghi",
        "abc/def/ghi/ijk",

        // These paths look really strange but Linux allows creating them.
        // I cannot see any good reason for forbidding them.
        "**",
        "...",
        ".../abc",
        "abc/...",
        "abc/.../def");
  }

  @ParameterizedTest
  @MethodSource("listOfInvalidProjectPaths")
  public void illegal_project_paths_are_reported(String path) {
    assertCall(() -> validatedProjectPath(container(), "name", string(path)))
        .throwsException(AbortException.class);
  }

  public static Stream<String> listOfInvalidProjectPaths() {
    return Stream.of(
        "",

        "./",
        "./.",
        "././",

        "abc/",
        "abc/def/",
        "abc/def/ghi/",

        "./abc",
        "./abc/def",
        "./abc/def/ghi",

        "..",
        "../",
        "./../",
        "../abc",
        "abc/..",
        "abc/../def",
        "../..",

        "/",
        "///",

        "/abc",
        "///abc",

        "abc//",
        "abc///",

        "abc//def",
        "abc///def");
  }
}

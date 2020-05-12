package org.smoothbuild.slib.file;

import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class PathArgValidatorTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("listOfCorrectProjectPaths")
  public void valid_project_paths_are_accepted(String path) {
    validatedProjectPath(container(), "name", string(path));
  }

  public static List<String> listOfCorrectProjectPaths() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add("**");

    builder.add("abc");
    builder.add("abc/def");
    builder.add("abc/def/ghi");
    builder.add("abc/def/ghi/ijk");

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add("...");
    builder.add(".../abc");
    builder.add("abc/...");
    builder.add("abc/.../def");

    return builder.build();
  }

  @ParameterizedTest
  @MethodSource("listOfInvalidProjectPaths")
  public void illegal_project_paths_are_reported(String path) {
    assertCall(() -> validatedProjectPath(container(), "name", string(path)))
        .throwsException(AbortException.class);
  }

  public static ImmutableList<String> listOfInvalidProjectPaths() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add("");
    builder.add(".");

    builder.add("./");
    builder.add("./.");
    builder.add("././");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");

    builder.add("..");
    builder.add("../");
    builder.add("./../");
    builder.add("../abc");
    builder.add("abc/..");
    builder.add("abc/../def");
    builder.add("../..");

    builder.add("/");
    builder.add("///");

    builder.add("/abc");
    builder.add("///abc");

    builder.add("abc//");
    builder.add("abc///");

    builder.add("abc//def");
    builder.add("abc///def");

    return builder.build();
  }
}

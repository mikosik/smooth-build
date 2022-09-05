package org.smoothbuild.slib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.run.eval.MessageStruct.messageSeverity;
import static org.smoothbuild.run.eval.MessageStruct.messageText;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.testing.TestContext;

public class PathArgValidatorTest extends TestContext {
  @ParameterizedTest
  @MethodSource("listOfCorrectProjectPaths")
  public void valid_project_paths_are_accepted(String path) {
    validatedProjectPath(container(), "name", stringB(path));
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
    PathS name = validatedProjectPath(container(), "name", stringB(path));
    assertThat(name)
        .isNull();
    container().messages()
        .elems(TupleB.class)
        .forEach(s -> {
          assertThat(messageText(s).toJ())
              .startsWith("Param `name` has illegal value.");
          assertThat(messageSeverity(s).toJ())
              .isEqualTo(ERROR.name());
        });
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

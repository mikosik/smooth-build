package org.smoothbuild.slib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.exec.base.MessageStruct.messageSeverity;
import static org.smoothbuild.exec.base.MessageStruct.messageText;
import static org.smoothbuild.slib.file.PathArgValidator.validatedProjectPath;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.io.fs.base.Path;
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
    Path name = validatedProjectPath(container(), "name", string(path));
    assertThat(name)
        .isNull();
    container().messages()
        .elements(Struc_.class)
        .forEach(s -> {
          assertThat(messageText(s).jValue())
              .startsWith("Param `name` has illegal value.");
          assertThat(messageSeverity(s).jValue())
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

package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.run.eval.MessageStruct.messageSeverity;
import static org.smoothbuild.run.eval.MessageStruct.messageText;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class PathArgValidatorTest extends TestContext {
  @ParameterizedTest
  @MethodSource("listOfCorrectProjectPaths")
  public void valid_project_paths_are_accepted(String path) throws Exception {
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
  public void illegal_project_paths_are_reported(String path) throws Exception {
    PathS name = validatedProjectPath(container(), "name", stringB(path));
    assertThat(name).isNull();
    var elements = container().messages().elems(TupleB.class);
    elements.map(e -> messageText(e).toJ()).forEach(t -> assertThat(t)
        .startsWith("Param `name` has illegal value."));
    elements.map(e -> messageSeverity(e).toJ()).forEach(s -> assertThat(s)
        .startsWith(ERROR.name()));
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

package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.stdlib.file.PathArgValidator.validatedProjectPath;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.storedLogLevel;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.storedLogMessage;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class PathArgValidatorTest extends VmTestContext {
  @ParameterizedTest
  @MethodSource("listOfCorrectProjectPaths")
  public void valid_project_paths_are_accepted(String path) throws Exception {
    validatedProjectPath(container(), "name", bString(path));
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
    Path name = validatedProjectPath(container(), "name", bString(path));
    assertThat(name).isNull();
    var elements = container().messages().elements(BTuple.class);
    elements.map(e -> storedLogMessage(e).toJavaString()).forEach(t -> assertThat(t)
        .startsWith("Param `name` has illegal value."));
    elements.map(e -> storedLogLevel(e).toJavaString()).forEach(s -> assertThat(s)
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

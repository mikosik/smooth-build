package org.smoothbuild.exec.java;

import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.exec.java.MethodPath.MethodPathParsingException;

public class MethodPathTest {
  @ParameterizedTest
  @MethodSource("illegal_path_test_data")
  public void path_with_dot_at_the_beginning(String path) {
    assertCall(() -> MethodPath.parse(path))
        .throwsException(new MethodPathParsingException("Illegal path to java method. " +
            "Expected <binary class name>.<method name>, but was `" + path + "`."));
  }

  private static List<String> illegal_path_test_data() {
    return list(
        "abc",
        ".abc",
        "abc."
    );
  }
}

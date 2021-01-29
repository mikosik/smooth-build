package org.smoothbuild.exec.nativ;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.exec.nativ.JavaMethodPath.JavaMethodPathParsingException;

public class JavaMethodPathTest {
  @ParameterizedTest
  @MethodSource("illegal_path_test_data")
  public void path_with_dot_at_the_beginning(String path) {
    assertCall(() -> JavaMethodPath.parse(path))
        .throwsException(new JavaMethodPathParsingException("Illegal path to java method. " +
            "Expected <binary class name>.<method name>, but was `" + path + "`."));
  }

  private static List<String> illegal_path_test_data() {
    return List.of(
        "abc",
        ".abc",
        "abc."
    );
  }
}

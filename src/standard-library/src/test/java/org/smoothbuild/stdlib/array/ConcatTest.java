package org.smoothbuild.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ConcatTest extends StandardLibraryTestContext {
  @Test
  void concat_empty_array() throws Exception {
    var userModule = """
        [Int] result = concat([[]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bIntType()));
  }

  @Test
  void concat_array_with_one_elem() throws Exception {
    var userModule = """
        result = concat([["a", "b", "c"]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bString("a"), bString("b"), bString("c")));
  }

  @Test
  void concat_array_with_two_elements() throws Exception {
    var userModule = """
        result = concat([["a", "b", "c"], ["d", "e", "f"]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(bArray(
            bString("a"), bString("b"), bString("c"), bString("d"), bString("e"), bString("f")));
  }
}

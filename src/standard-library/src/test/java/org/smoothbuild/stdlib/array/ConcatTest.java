package org.smoothbuild.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class ConcatTest extends StandardLibraryTestCase {
  @Test
  public void concat_empty_array() throws Exception {
    var userModule = """
        [Int] result = concat([[]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(arrayB(intTB()));
  }

  @Test
  public void concat_array_with_one_elem() throws Exception {
    var userModule = """
        result = concat([["a", "b", "c"]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(arrayB(stringB("a"), stringB("b"), stringB("c")));
  }

  @Test
  public void concat_array_with_two_elements() throws Exception {
    var userModule = """
        result = concat([["a", "b", "c"], ["d", "e", "f"]]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(arrayB(
            stringB("a"), stringB("b"), stringB("c"), stringB("d"), stringB("e"), stringB("f")));
  }
}

package org.smoothbuild.stdlib.array;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ZipTest extends StandardLibraryTestContext {
  @Test
  void zip_empty_arrays() throws Exception {
    var userModule = """
        [{Int,String}] result = zip([], []);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bTupleType(bIntType(), bStringType())));
  }

  @Test
  void zip_arrays_of_same_length() throws Exception {
    var userModule = """
        result = zip([1, 2, 3], ["a", "b", "c"]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(bArray(
            bTuple(bInt(1), bString("a")),
            bTuple(bInt(2), bString("b")),
            bTuple(bInt(3), bString("c"))));
  }

  @Test
  void zip_first_array_shorter() throws Exception {
    var userModule = """
        result = zip([1, 2], ["a", "b", "c"]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(bArray(bTuple(bInt(1), bString("a")), bTuple(bInt(2), bString("b"))));
  }

  @Test
  void zip_second_array_shorter() throws Exception {
    var userModule = """
        result = zip([1, 2, 3], ["a", "b"]);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(bArray(bTuple(bInt(1), bString("a")), bTuple(bInt(2), bString("b"))));
  }
}

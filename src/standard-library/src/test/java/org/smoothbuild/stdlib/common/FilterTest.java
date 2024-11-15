package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FilterTest extends StandardLibraryTestContext {
  @Test
  void filter_returns_filtered_list() throws Exception {
    var userModule = """
        result = filter([1, 2, 3], (Int i) -> equal(i, 2));
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bInt(2)));
  }

  @Test
  void true_predicate_returns_whole_list() throws Exception {
    var userModule = """
        result = filter([1, 2, 3], (A a) -> true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bInt(1), bInt(2), bInt(3)));
  }

  @Test
  void false_predicate_returns_empty_list() throws Exception {
    var userModule = """
        result = filter([1, 2, 3], (A a) -> false);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bIntType()));
  }

  @Test
  void empty_list_is_filtered_to_empty_list() throws Exception {
    var userModule = """
        [Int] result = filter([], (A i) -> true);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bIntType()));
  }
}

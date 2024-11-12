package org.smoothbuild.stdlib.array;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class ElemTest extends StandardLibraryTestContext {
  @Test
  void first_element() throws Exception {
    var userModule = """
        result = elem(["first", "second", "third"], 0);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("first"));
  }

  @Test
  void last_element() throws Exception {
    var userModule = """
        result = elem(["first", "second", "third"], 2);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("third"));
  }

  @Test
  void index_out_of_bounds_causes_exception() throws Exception {
    var userModule = """
        result = elem(["first", "second", "third"], 3);
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("Index (3) out of bounds. Array size = 3."));
  }

  @Test
  void negative_index_causes_exception() throws Exception {
    createUserModule(
        """
            result = elem(["first", "second", "third"], -1);
            """);
    evaluate("result");
    assertThat(logs()).contains(error("Index (-1) out of bounds. Array size = 3."));
  }
}

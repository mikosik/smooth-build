package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ConcatStrings;

public class FoldTest extends StandardLibraryTestContext {
  @Test
  void folding_strings_with_concat() throws Exception {
    var code = String.format(
        """
            @Native("%s")
            String concatStrings(String a, String b);
            result = fold(["a", "b", "c"], "", concatStrings);
            """,
        ConcatStrings.class.getCanonicalName());
    createUserModule(code, ConcatStrings.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("abc"));
  }

  @Test
  void folding_empty_array_returns_initial_value() throws Exception {
    var code = String.format(
        """
            @Native("%s")
            String concatStrings(String a, String b);
            result = fold([], "initial", concatStrings);
            """,
        ConcatStrings.class.getCanonicalName());
    createUserModule(code, ConcatStrings.class);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bString("initial"));
  }

  @Test
  void and_booleans() throws Exception {
    var code = """
        result = fold([true, true, false, true], true, and);
        """;
    createUserModule(code);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bBool(false));
  }
}

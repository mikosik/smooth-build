package org.smoothbuild.stdlib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class EqualTest extends StandardLibraryTestCase {
  @Nested
  class _int {
    @Test
    void int_is_equal_to_itself() throws Exception {
      var userModule = """
          result = equal(7, 7);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void int_is_not_equal_to_different_int() throws Exception {
      var userModule = """
          result = equal(7, 17);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }
  }

  @Nested
  class _string {
    @Test
    void string_is_equal_to_itself() throws Exception {
      var userModule = """
          result = equal("aaa", "aaa");
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void string_is_not_equal_to_different_string() throws Exception {
      var userModule = """
          result = equal("aaa", "bbb");
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }

    @Test
    void empty_string_is_equal_to_itself() throws Exception {
      var userModule = """
          result = equal("", "");
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void empty_string_is_not_equal_to_non_empty_string() throws Exception {
      var userModule = """
          result = equal("aaa", "");
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }
  }

  @Nested
  class _bool {
    @Test
    void true_is_equal_to_true() throws Exception {
      var userModule = """
          result = equal(true, true);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void false_is_equal_to_false() throws Exception {
      var userModule = """
          result = equal(false, false);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void true_is_not_equal_to_false() throws Exception {
      var userModule = """
          result = equal(true, false);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }

    @Test
    void false_is_not_equal_to_true() throws Exception {
      var userModule = """
          result = equal(false, true);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }
  }

  @Nested
  class _struct {
    @Test
    void struct_is_equal_to_itself() throws Exception {
      var userModule =
          """
          Person {
            String firstName,
            String secondName,
          }
          result = equal(Person("aaa", "bbb"), Person("aaa", "bbb"));
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws Exception {
      var userModule =
          """
          Person {
            String firstName,
            String secondName,
          }
          result = equal(Person("aaa", "bbb"), Person("aaa", "ccc"));
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(false));
    }
  }

  @Nested
  class _array {
    @Test
    void empty_arrays_are_equal() throws Exception {
      var userModule = """
          result = equal([], []);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void int_array_is_equal_to_itself() throws Exception {
      var userModule = """
          result = equal([7, 17], [7, 17]);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }

    @Test
    void string_array_is_equal_to_itself() throws Exception {
      var userModule = """
          result = equal(["aaa", "bbb"], ["aaa", "bbb"]);
          """;
      createUserModule(userModule);
      evaluate("result");
      assertThat(artifact()).isEqualTo(bBool(true));
    }
  }
}

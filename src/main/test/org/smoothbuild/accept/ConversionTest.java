package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;

public class ConversionTest extends AcceptanceTestCase {
  @Nested
  class _default_argument {
    @Test
    public void nothing_array_to_base_type_array() throws Exception {
      createUserModule("""
        [Int] myFunc([Int] array = []) = array;
        result = myFunc();
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void nothing_array2_to_base_type_array2() throws Exception {
      createUserModule("""
        [[Int]] myFunc([[Int]] array = [[]]) = array;
        result = myFunc();
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(arrayB(intTB())));
    }
  }

  @Nested
  class _func_result {
    @Test
    public void nothing_array_to_base_type_array() throws Exception {
      createUserModule("""
        [Int] myFunc() = [];
        result = myFunc();
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void nothing_array2_to_base_type_array2() throws Exception {
      createUserModule("""
        [[Int]] myFunc() = [[]];
        result = myFunc();
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(arrayB(intTB())));
    }
  }

  @Nested
  class _named_argument {
    @Test
    public void nothing_array_to_base_type_array() throws Exception {
      createUserModule("""
        [Int] myFunc([Int] array) = array;
        result = myFunc(array = []);
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void nothing_array2_to_base_type_array2() throws Exception {
      createUserModule("""
        [[Int]] myFunc([[Int]] array) = array;
        result = myFunc(array = [[]]);
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(arrayB(intTB())));
    }
  }

  @Nested
  class _positional_argument {
    @Test
    public void nothing_array_to_base_type_array() throws Exception {
      createUserModule("""
        [Int] myFunc([Int] array) = array;
        result = myFunc([]);
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void nothing_array2_to_base_type_array2() throws Exception {
      createUserModule("""
        [[Int]] myFunc([[Int]] array) = array;
        result = myFunc([[]]);
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(arrayB(intTB())));
    }
  }

  @Nested
  class _value_result {
    @Test
    public void nothing_array_to_base_type_array() throws Exception {
      createUserModule("""
        [Int] result = [];
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(intTB()));
    }

    @Test
    public void nothing_array2_to_base_type_array2() throws Exception {
      createUserModule("""
        [[Int]] result = [[]];
        """);
      evaluate("result");
      assertThat(artifact())
          .isEqualTo(arrayB(arrayB(intTB())));
    }
  }
}

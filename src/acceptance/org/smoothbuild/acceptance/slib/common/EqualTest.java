package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class EqualTest extends AcceptanceTestCase {
  @Nested
  class _string {
    @Test
    public void string_is_equal_to_itself() throws Exception {
      createUserModule("""
          result = equal("aaa", "aaa");
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void string_is_not_equal_to_different_string() throws Exception {
      createUserModule("""
          result = equal("aaa", "bbb");
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }

    @Test
    public void empty_string_is_equal_to_itself() throws Exception {
      createUserModule("""
          result = equal("", "");
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void empty_string_is_not_equal_to_non_empty_string() throws Exception {
      createUserModule("""
          result = equal("aaa", "");
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }

    @Test
    public void string_is_not_equal_to_bool() throws Exception {
      createUserModule("""
          result = equal("aaa", true);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }

    @Test
    public void empty_string_is_not_equal_to_empty_array() throws Exception {
      createUserModule("""
          result = equal("", []);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_is_equal_to_true() throws Exception {
      createUserModule("""
          result = equal(true, true);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void false_is_equal_to_false() throws Exception {
      createUserModule("""
          result = equal(false, false);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void true_is_not_equal_to_false() throws Exception {
      createUserModule("""
          result = equal(true, false);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }

    @Test
    public void false_is_not_equal_to_true() throws Exception {
      createUserModule("""
          result = equal(false, true);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }
  }

  @Nested
  class _struct {
    @Test
    public void struct_is_equal_to_itself() throws Exception {
      createUserModule("""
          Person {
            String firstName,
            String secondName,
          }
          result = equal(person("aaa", "bbb"), person("aaa", "bbb"));
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws
        Exception {
      createUserModule("""
          Person {
            String firstName,
            String secondName,
          }
          result = equal(person("aaa", "bbb"), person("aaa", "ccc"));
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(false);
    }

    @Test
    public void struct_is_equal_to_different_struct_type_with_the_same_low_level_representation()
        throws Exception {
      createUserModule("""
          Vector {
            String x,
            String y,
          }
          Tuple {
            String a,
            String b,
          }
          result = equal(vector("aaa", "bbb"), tuple("aaa", "bbb"));
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }
  }

  @Nested
  class _array {
    @Test
    public void string_array_is_equal_to_itself() throws Exception {
      createUserModule("""
          result = equal([ "aaa", "bbb" ], [ "aaa", "bbb" ]);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void empty_nothing_array_is_equal_to_itself() throws Exception {
      createUserModule("""
          result = equal([], []);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }

    @Test
    public void empty_nothing_arrays_is_equal_to_empty_string_array() throws Exception {
      createUserModule("""
          [Nothing] nothingArray = [];
          [String] stringArray = [];
          result = equal(nothingArray, stringArray);
          """);
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(artifactAsBoolean("result"))
          .isEqualTo(true);
    }
  }
}

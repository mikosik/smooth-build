package org.smoothbuild.acceptance.slib.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class EqualTest extends AcceptanceTestCase {
  @Test
  public void string_is_equal_to_itself() throws Exception {
    createUserModule(
        "  result = equal('aaa', 'aaa');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void string_is_not_equal_to_different_string() throws Exception {
    createUserModule(
        "  result = equal('aaa', 'bbb');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void empty_string_and_non_empty_are_not_equal() throws Exception {
    createUserModule(
        "  result = equal('aaa', '');  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void true_bool_values_are_equal() throws Exception {
    createUserModule(
        "  result = equal([ 'aaa', 'bbb' ], [ 'aaa', 'bbb' ]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void false_bool_values_are_equal() throws Exception {
    createUserModule(
        "  result = equal(false(), false());  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_is_not_equal_to_false() throws Exception {
    createUserModule(
        "  result = equal(true(), false());  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void struct_is_equal_to_itself() throws Exception {
    createUserModule(
        "  Person {                                                     ",
        "    String firstName,                                          ",
        "    String secondName,                                         ",
        "  }                                                            ",
        "  result = equal(person('aaa', 'bbb'), person('aaa', 'bbb'));  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws Exception {
    createUserModule(
        "  Person {                                                     ",
        "    String firstName,                                          ",
        "    String secondName,                                         ",
        "  }                                                            ",
        "  result = equal(person('aaa', 'bbb'), person('aaa', 'ccc'));  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void struct_is_equal_to_different_struct_when_their_first_fields_are_equal() throws
      Exception {
    // This is caused by the fact that struct can be auto-converted to value of its first field.
    createUserModule(
        "  Person {                                                      ",
        "    String firstName,                                           ",
        "    String secondName,                                          ",
        "  }                                                             ",
        "  Person2 {                                                     ",
        "    String firstName,                                           ",
        "  }                                                             ",
        "                                                                ",
        "  result = equal(person('aaa', 'bbb'), person2('aaa'));  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void struct_cannot_be_compared_to_different_struct_when_their_first_field_types_are_not_equal()
      throws Exception {
    createUserModule("""
            Person {
              String firstName,
              String secondName,
            }
            Person2 {
              Bool firstName,
              String secondName,
            }
            result = equal(person("aaa", "bbb"), person2(true(), "bbb"));
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContainsParseError(
        9, "Cannot infer actual type(s) for generic parameter(s) in call to 'equal'.");
  }

  @Test
  public void string_array_is_equal_to_itself() throws Exception {
    createUserModule(
        "  result = equal([ 'aaa', 'bbb' ], [ 'aaa', 'bbb' ]);  ");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void empty_nothing_array_is_equal_to_itself() throws Exception {
    createUserModule(
        "  result = equal([], []);  ");
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

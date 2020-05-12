package org.smoothbuild.slib.builtin.common;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.slib.AcceptanceTestCase;

public class EqualTest extends AcceptanceTestCase {
  @Test
  public void string_is_equal_to_itself() throws Exception {
    givenScript(
        "  result = equal('aaa', 'aaa');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void string_is_not_equal_to_different_string() throws Exception {
    givenScript(
        "  result = equal('aaa', 'bbb');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void empty_string_and_non_empty_are_not_equal() throws Exception {
    givenScript(
        "  result = equal('aaa', '');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void true_bool_values_are_equal() throws Exception {
    givenScript(
        "  result = equal([ 'aaa', 'bbb' ], [ 'aaa', 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void false_bool_values_are_equal() throws Exception {
    givenScript(
        "  result = equal(false(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void true_is_not_equal_to_false() throws Exception {
    givenScript(
        "  result = equal(true(), false());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void struct_is_equal_to_itself() throws Exception {
    givenScript(
        "  Person {                                                     ",
        "    String firstName,                                          ",
        "    String secondName,                                         ",
        "  }                                                            ",
        "  result = equal(person('aaa', 'bbb'), person('aaa', 'bbb'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws Exception {
    givenScript(
        "  Person {                                                     ",
        "    String firstName,                                          ",
        "    String secondName,                                         ",
        "  }                                                            ",
        "  result = equal(person('aaa', 'bbb'), person('aaa', 'ccc'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(false);
  }

  @Test
  public void struct_is_equal_to_different_struct_when_their_first_fields_are_equal() throws
      Exception {
    // This is caused by the fact that struct can be auto-converted to value of its first field.
    givenScript(
        "  Person {                                                      ",
        "    String firstName,                                           ",
        "    String secondName,                                          ",
        "  }                                                             ",
        "  Person2 {                                                     ",
        "    String firstName,                                           ",
        "  }                                                             ",
        "                                                                ",
        "  result = equal(person('aaa', 'bbb'), person2('aaa'));  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void struct_cannot_be_compared_to_different_struct_when_their_first_field_types_are_not_equal()
      throws Exception {
    givenScript(
        "  Person {                                                      ",
        "    String firstName,                                           ",
        "    String secondName,                                          ",
        "  }                                                             ",
        "  Person2 {                                                     ",
        "    Bool   firstName,                                           ",
        "    String secondName,                                          ",
        "  }                                                             ",
        "                                                                ",
        "  result = equal(person('aaa', 'bbb'), person2(true, 'bbb'));   ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContainsParseError(
        10, "Cannot infer actual type(s) for generic parameter(s) in call to 'equal'.");
  }

  @Test
  public void string_array_is_equal_to_itself() throws Exception {
    givenScript(
        "  result = equal([ 'aaa', 'bbb' ], [ 'aaa', 'bbb' ]);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void empty_nothing_array_is_equal_to_itself() throws Exception {
    givenScript(
        "  result = equal([], []);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }

  @Test
  public void empty_nothing_arrays_is_equal_to_empty_string_array() throws Exception {
    givenScript(
        "  [Nothing] nothingArray = [];                ",
        "  [String] stringArray = [];                  ",
        "  result = equal(nothingArray, stringArray);  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactAsBoolean("result"))
        .isEqualTo(true);
  }
}

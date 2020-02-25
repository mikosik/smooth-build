package org.smoothbuild.acceptance.builtin.common;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class EqualTest extends AcceptanceTestCase {
  @Test
  public void string_is_equal_to_itself() throws Exception {
    givenScript("result = equal('aaa', 'aaa');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void string_is_not_equal_to_different_string() throws Exception {
    givenScript("result = equal('aaa', 'bbb');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }

  @Test
  public void empty_string_and_non_empty_are_not_equal() throws Exception {
    givenScript("result = equal('aaa', '');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }

  @Test
  public void true_bool_values_are_equal() throws Exception {
    givenScript("result = equal(true(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void false_bool_values_are_equal() throws Exception {
    givenScript("result = equal(false(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void true_is_not_equal_to_false() throws Exception {
    givenScript("result = equal(true(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }

  @Test
  public void struct_is_equal_to_itself() throws Exception {
    givenScript("Person {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        result = equal(person('aaa', 'bbb'), person('aaa', 'bbb'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws Exception {
    givenScript("Person {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        result = equal(person('aaa', 'bbb'), person('aaa', 'ccc'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }

  @Test
  public void struct_is_not_equal_to_struct_with_same_field_values_but_different_type() throws
      Exception {
    givenScript("Person {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        Person2 {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        " +
        "        result = equal(person('aaa', 'bbb'), person2('aaa', 'aaa'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }

  @Test
  public void string_array_is_equal_to_itself() throws Exception {
    givenScript("result = equal(['aaa', 'bbb'], ['aaa', 'bbb']);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void empty_nothing_array_is_equal_to_itself() throws Exception {
    givenScript("result = equal([], []);");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(true));
  }

  @Test
  public void empty_arrays_of_different_types_are_not_equal() throws Exception {
    givenScript("[String] stringArray = [];     \n" +
        "        [Bool] boolArray = [];         \n" +
        "        result = equal(stringArray, boolArray);        \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsBoolean("result"), equalTo(false));
  }
}

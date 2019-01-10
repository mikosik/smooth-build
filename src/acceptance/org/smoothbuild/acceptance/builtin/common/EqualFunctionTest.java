package org.smoothbuild.acceptance.builtin.common;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

import okio.ByteString;

public class EqualFunctionTest extends AcceptanceTestCase {
  @Test
  public void string_is_equal_to_itself() throws Exception {
    givenScript("result = equal('aaa', 'aaa');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 1)));
  }

  @Test
  public void string_is_not_equal_to_different_string() throws Exception {
    givenScript("result = equal('aaa', 'bbb');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 0)));
  }

  @Test
  public void true_bool_values_are_equal() throws Exception {
    givenScript("result = equal(true(), true());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 1)));
  }

  @Test
  public void false_bool_values_are_equal() throws Exception {
    givenScript("result = equal(false(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 1)));
  }

  @Test
  public void bool_is_not_equal_to_different_bool() throws Exception {
    givenScript("result = equal(true(), false());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 0)));
  }

  @Test
  public void struct_is_equal_to_itself() throws Exception {
    givenScript("Person {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        result = equal(Person('aaa', 'bbb'), Person('aaa', 'bbb'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 1)));
  }

  @Test
  public void struct_is_not_equal_to_the_same_struct_with_different_field_value() throws Exception {
    givenScript("Person {" +
        "          String firstName," +
        "          String secondName," +
        "        }" +
        "        result = equal(Person('aaa', 'bbb'), Person('aaa', 'ccc'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 0)));
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
        "        result = equal(Person('aaa', 'bbb'), Person2('aaa', 'aaa'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactAsByteStrings("result"), equalTo(ByteString.of((byte) 0)));
  }
}

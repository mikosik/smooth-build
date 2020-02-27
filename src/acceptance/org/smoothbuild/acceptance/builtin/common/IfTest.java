package org.smoothbuild.acceptance.builtin.common;

import static org.hamcrest.Matchers.equalTo;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ThrowException;

public class IfTest extends AcceptanceTestCase {
  @Test
  public void if_returns_first_value_when_condition_is_true() throws Exception {
    givenScript(
        "  result = if(true(), 'then clause', 'else clause');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("then clause"));
  }

  @Test
  public void if_returns_second_value_when_condition_is_false() throws Exception {
    givenScript(
        "  result = if(false(), 'then clause', 'else clause');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("else clause"));
  }

  @Test
  public void first_value_should_not_be_evaluated_when_condition_is_false() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();                               ",
        "  result = if(false(), throwException(), 'else clause');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("else clause"));
  }

  @Test
  public void second_value_should_not_be_evaluated_when_condition_is_true() throws Exception {
    givenNativeJar(ThrowException.class);
    givenScript(
        "  Nothing throwException();                              ",
        "  result = if(true(), 'then clause', throwException());  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("then clause"));
  }
}

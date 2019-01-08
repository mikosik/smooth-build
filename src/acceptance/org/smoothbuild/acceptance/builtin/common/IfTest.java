package org.smoothbuild.acceptance.builtin.common;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.testing.db.values.ValueCreators.falseByteString;
import static org.smoothbuild.testing.db.values.ValueCreators.trueByteString;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class IfTest extends AcceptanceTestCase {
  @Test
  public void if_returns_first_value_when_condition_is_true() throws Exception {
    givenScript("result = if(true(), 'then clause', 'else clause');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("then clause"));
  }

  @Test
  public void if_returns_second_value_when_condition_is_true() throws Exception {
    givenScript("result = if(false(), 'then clause', 'else clause');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifactContent("result"), equalTo("else clause"));
  }
}

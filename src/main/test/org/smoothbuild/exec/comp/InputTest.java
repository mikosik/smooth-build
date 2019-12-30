package org.smoothbuild.exec.comp;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.testing.TestingContext;

public class InputTest extends TestingContext{
  private Input input1;
  private Input input2;
  private SString sstring1;
  private SString sstring2;

  @Test
  public void different_inputs_have_different_hashes() {
    given(sstring1 = string("abc"));
    given(sstring2 = string("def"));
    given(input1 = Input.fromObjects(list(sstring1)));
    given(input2 = Input.fromObjects(list(sstring2)));
    when(input1).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes() {
    given(sstring1 = string("abc"));
    given(sstring2 = string("def"));
    given(input1 = Input.fromObjects(list(sstring1, sstring2)));
    given(input2 = Input.fromObjects(list(sstring2, sstring1)));
    when(input1).hash();
    thenReturned(not(input2.hash()));
  }

  @Test
  public void equal_inputs_have_equal_hashes() {
    given(sstring1 = string("abc"));
    given(input1 = Input.fromObjects(list(sstring1)));
    given(input2 = Input.fromObjects(list(sstring1)));
    when(input1).hash();
    thenReturned(input2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() {
    given(sstring1 = string("abc"));
    given(input1 = Input.fromObjects(list(sstring1)));
    given(input2 = Input.fromObjects(list()));
    when(input1).hash();
    thenReturned(not(input2.hash()));
  }
}

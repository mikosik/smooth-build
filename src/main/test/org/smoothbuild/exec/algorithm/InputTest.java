package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.algorithm.Input.input;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.testing.TestingContext;

public class InputTest extends TestingContext{
  private Input input1;
  private Input input2;
  private RString rstring1;
  private RString rstring2;

  @Test
  public void different_inputs_have_different_hashes() {
    rstring1 = string("abc");
    rstring2 = string("def");
    input1 = input(list(rstring1));
    input2 = input(list(rstring2));

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes() {
    rstring1 = string("abc");
    rstring2 = string("def");
    input1 = input(list(rstring1, rstring2));
    input2 = input(list(rstring2, rstring1));

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }

  @Test
  public void equal_inputs_have_equal_hashes() {
    rstring1 = string("abc");
    input1 = input(list(rstring1));
    input2 = input(list(rstring1));

    assertThat(input1.hash())
        .isEqualTo(input2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() {
    rstring1 = string("abc");
    input1 = input(list(rstring1));
    input2 = input(list());

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }
}

package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class InputTest extends TestingContext {
  @Test
  public void different_inputs_have_different_hashes() {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var input1 = input(list(string1));
    var input2 = input(list(string2));

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes() {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var input1 = input(list(string1, string2));
    var input2 = input(list(string2, string1));

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }

  @Test
  public void equal_inputs_have_equal_hashes() {
    var string1 = stringB("abc");
    var input1 = input(list(string1));
    var input2 = input(list(string1));

    assertThat(input1.hash())
        .isEqualTo(input2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() {
    var string1 = stringB("abc");
    var input1 = input(list(string1));
    var input2 = input(list());

    assertThat(input1.hash())
        .isNotEqualTo(input2.hash());
  }
}

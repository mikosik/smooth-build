package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class FuncBTest extends TestingContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTB(intTB(), list(stringTB()));
    assertCall(() -> funcB(funcT, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTB(intTB(), list(boolTB()));
    assertCall(() -> funcB(funcT, null))
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTB(intTB(), list(stringTB()));
    assertThat(funcB(funcT, intB()).cat())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_combineion() {
    var funcT = funcTB(intTB(), list(boolTB()));
    var body = intB(33);
    assertThat(funcB(funcT, body).body())
        .isEqualTo(body);
  }

  @Test
  public void funcs_with_equal_body_are_equal() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func1 = funcB(funcT, intB());
    var func2 = funcB(funcT, intB());
    assertThat(func1)
        .isEqualTo(func2);
  }

  @Test
  public void funcs_with_different_body_are_not_equal() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func1 = funcB(funcT, intB(1));
    var func2 = funcB(funcT, intB(2));
    assertThat(func1)
        .isNotEqualTo(func2);
  }

  @Test
  public void funcs_with_equal_body_have_equal_hashes() {
    var funcT = funcTB(intTB(), list(intTB()));
    var func1 = funcB(funcT, intB());
    var func2 = funcB(funcT, intB());
    assertThat(func1.hash())
        .isEqualTo(func2.hash());
  }

  @Test
  public void funcs_with_different_bodies_have_not_equal_hashes() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func1 = funcB(funcT, intB(1));
    var func2 = funcB(funcT, intB(2));
    assertThat(func1.hash())
        .isNotEqualTo(func2.hash());
  }

  @Test
  public void funcs_with_equal_body_have_equal_hash_code() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func1 = funcB(funcT, intB());
    var func2 = funcB(funcT, intB());
    assertThat(func1.hashCode())
        .isEqualTo(func2.hashCode());
  }

  @Test
  public void funcs_with_different_bodies_have_not_equal_hash_code() {
    var funcT = funcTB(intTB(), list(intTB()));
    var func1 = funcB(funcT, intB(1));
    var func2 = funcB(funcT, intB(2));
    assertThat(func1.hashCode())
        .isNotEqualTo(func2.hashCode());
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func = funcB(funcT, intB());
    assertThat(byteDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func = funcB(funcT, intB());
    var funcRead = (FuncB) byteDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTB(intTB(), list(stringTB()));
    var func = funcB(funcT, intB());
    assertThat(func.toString())
        .isEqualTo("Func(Int(String))@" + func.hash());
  }
}

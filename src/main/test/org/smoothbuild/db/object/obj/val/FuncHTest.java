package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class FuncHTest extends TestingContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTH(intTH(), list(stringTH()));
    assertCall(() -> funcH(funcT, boolH(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTH(intTH(), list(boolTH()));
    assertCall(() -> funcH(funcT, null))
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTH(intTH(), list(stringTH()));
    assertThat(funcH(funcT, intH()).cat())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_combineion() {
    var funcT = funcTH(intTH(), list(boolTH()));
    var body = intH(33);
    assertThat(funcH(funcT, body).body())
        .isEqualTo(body);
  }

  @Test
  public void funcs_with_equal_body_are_equal() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func1 = funcH(funcT, intH());
    var func2 = funcH(funcT, intH());
    assertThat(func1)
        .isEqualTo(func2);
  }

  @Test
  public void funcs_with_different_body_are_not_equal() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func1 = funcH(funcT, intH(1));
    var func2 = funcH(funcT, intH(2));
    assertThat(func1)
        .isNotEqualTo(func2);
  }

  @Test
  public void funcs_with_equal_body_have_equal_hashes() {
    var funcT = funcTH(intTH(), list(intTH()));
    var func1 = funcH(funcT, intH());
    var func2 = funcH(funcT, intH());
    assertThat(func1.hash())
        .isEqualTo(func2.hash());
  }

  @Test
  public void funcs_with_different_bodies_have_not_equal_hashes() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func1 = funcH(funcT, intH(1));
    var func2 = funcH(funcT, intH(2));
    assertThat(func1.hash())
        .isNotEqualTo(func2.hash());
  }

  @Test
  public void funcs_with_equal_body_have_equal_hash_code() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func1 = funcH(funcT, intH());
    var func2 = funcH(funcT, intH());
    assertThat(func1.hashCode())
        .isEqualTo(func2.hashCode());
  }

  @Test
  public void funcs_with_different_bodies_have_not_equal_hash_code() {
    var funcT = funcTH(intTH(), list(intTH()));
    var func1 = funcH(funcT, intH(1));
    var func2 = funcH(funcT, intH(2));
    assertThat(func1.hashCode())
        .isNotEqualTo(func2.hashCode());
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func = funcH(funcT, intH());
    assertThat(objDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func = funcH(funcT, intH());
    var funcRead = (FuncH) objDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTH(intTH(), list(stringTH()));
    var func = funcH(funcT, intH());
    assertThat(func.toString())
        .isEqualTo("Func(Int(String))@" + func.hash());
  }
}

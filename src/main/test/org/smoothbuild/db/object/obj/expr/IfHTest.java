package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class IfHTest extends TestingContext {
  @Nested
  class _infer_type_of_if {
    @Test
    public void with_both_clauses_having_same_type() {
      assertThat(ifH(boolH(true), intH(1), intH(2)).cat())
          .isEqualTo(ifCH(intTH()));
    }

    @Test
    public void with_one_clauses_being_subtype_of_the_other() {
      assertThat(ifH(boolH(true), arrayH(stringTH()), arrayH(nothingTH())).cat())
          .isEqualTo(ifCH(arrayTH(stringTH())));
    }
  }

  @Test
  public void creating_if_with_condition_not_being_bool_causes_exception() {
    assertCall(() -> ifH(blobH(0), intH(1), intH(2)))
        .throwsException(new IllegalArgumentException(
            "`condition` component must evaluate to BoolH but is `Blob`."));
  }

  @Test
  public void condition_getter() {
    var ifH = ifH(boolH(true), intH(1), intH(2));
    assertThat(ifH.data().condition())
        .isEqualTo(boolH(true));
  }

  @Test
  public void then_getter() {
    var ifH = ifH(boolH(true), intH(1), intH(2));
    assertThat(ifH.data().then_())
        .isEqualTo(intH(1));
  }

  @Test
  public void else_getter() {
    var ifH = ifH(boolH(true), intH(1), intH(2));
    assertThat(ifH.data().else_())
        .isEqualTo(intH(2));
  }

  @Test
  public void if_with_equal_values_are_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition, then, else_))
        .isEqualTo(ifH(condition, then, else_));
  }

  @Test
  public void if_with_different_condition_are_not_equal() {
    var condition1 = boolH(true);
    var condition2 = boolH(false);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition1, then, else_))
        .isNotEqualTo(ifH(condition2, then, else_));
  }

  @Test
  public void if_with_different_then_are_not_equal() {
    var condition = boolH(true);
    var then1 = intH(1);
    var then2 = intH(7);
    var else_ = intH(2);
    assertThat(ifH(condition, then1, else_))
        .isNotEqualTo(ifH(condition, then2, else_));
  }

  @Test
  public void if_with_different_else_are_not_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else1 = intH(2);
    var else2 = intH(7);
    assertThat(ifH(condition, then, else1))
        .isNotEqualTo(ifH(condition, then, else2));
  }

  @Test
  public void hash_of_if_with_equal_values_are_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition, then, else_).hash())
        .isEqualTo(ifH(condition, then, else_).hash());
  }

  @Test
  public void hash_of_if_with_different_condition_are_not_equal() {
    var condition1 = boolH(true);
    var condition2 = boolH(false);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition1, then, else_).hash())
        .isNotEqualTo(ifH(condition2, then, else_).hash());
  }

  @Test
  public void hash_of_if_with_different_then_are_not_equal() {
    var condition = boolH(true);
    var then1 = intH(1);
    var then2 = intH(7);
    var else_ = intH(2);
    assertThat(ifH(condition, then1, else_).hash())
        .isNotEqualTo(ifH(condition, then2, else_).hash());
  }

  @Test
  public void hash_of_if_with_different_else_are_not_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else1 = intH(2);
    var else2 = intH(7);
    assertThat(ifH(condition, then, else1).hash())
        .isNotEqualTo(ifH(condition, then, else2).hash());
  }

  @Test
  public void hashCode_of_if_with_equal_values_are_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition, then, else_).hashCode())
        .isEqualTo(ifH(condition, then, else_).hashCode());
  }

  @Test
  public void hashCode_of_if_with_different_condition_are_not_equal() {
    var condition1 = boolH(true);
    var condition2 = boolH(false);
    var then = intH(1);
    var else_ = intH(2);
    assertThat(ifH(condition1, then, else_).hashCode())
        .isNotEqualTo(ifH(condition2, then, else_).hashCode());
  }

  @Test
  public void hashCode_of_if_with_different_then_are_not_equal() {
    var condition = boolH(true);
    var then1 = intH(1);
    var then2 = intH(7);
    var else_ = intH(2);
    assertThat(ifH(condition, then1, else_).hashCode())
        .isNotEqualTo(ifH(condition, then2, else_).hashCode());
  }

  @Test
  public void hashCode_of_if_with_different_else_are_not_equal() {
    var condition = boolH(true);
    var then = intH(1);
    var else1 = intH(2);
    var else2 = intH(7);
    assertThat(ifH(condition, then, else1).hashCode())
        .isNotEqualTo(ifH(condition, then, else2).hashCode());
  }

  @Test
  public void if_can_be_read_back_by_hash() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    var ifH = ifH(condition, then, else_);
    assertThat(objDbOther().get(ifH.hash()))
        .isEqualTo(ifH);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    var ifH = ifH(condition, then, else_);
    var readIf = (IfH) objDbOther().get(ifH.hash());
    var readIfData = readIf.data();
    var ifData = ifH.data();

    assertThat(readIfData.condition())
        .isEqualTo(ifData.condition());
    assertThat(readIfData.then_())
        .isEqualTo(ifData.then_());
    assertThat(readIfData.else_())
        .isEqualTo(ifData.else_());
  }

  @Test
  public void to_string() {
    var condition = boolH(true);
    var then = intH(1);
    var else_ = intH(2);
    var ifH = ifH(condition, then, else_);
    assertThat(ifH.toString())
        .isEqualTo("If:Int(???)@" + ifH.hash());
  }
}

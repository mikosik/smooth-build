package org.smoothbuild.bytecode.expr.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestContext;

public class BoolBTest extends TestContext {
  @Test
  public void type_of_bool_is_bool_type() {
    assertThat(boolB(true).cat())
        .isEqualTo(boolTB());
  }

  @Test
  public void to_j_returns_java_true_from_true_bool() {
    BoolB bool = boolB(true);
    assertThat(bool.toJ())
        .isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() {
    BoolB bool = boolB(false);
    assertThat(bool.toJ())
        .isFalse();
  }


  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<BoolB> {
    @Override
    protected List<BoolB> equalExprs() {
      return list(
          boolB(true),
          boolB(true)
      );
    }

    @Override
    protected List<BoolB> nonEqualExprs() {
      return list(
          boolB(true),
          boolB(false)
      );
    }
  }

  @Test
  public void bool_can_be_read_back_by_hash() {
    BoolB bool = boolB(true);
    Hash hash = bool.hash();
    assertThat(bytecodeDbOther().get(hash))
        .isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_to_j() {
    BoolB bool = boolB(true);
    assertThat(((BoolB) bytecodeDbOther().get(bool.hash())).toJ())
        .isTrue();
  }

  @Test
  public void to_string_contains_value() {
    BoolB bool = boolB(true);
    assertThat(bool.toString())
        .isEqualTo("true@" + bool.hash());
  }
}

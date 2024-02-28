package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class BoolBTest extends TestContext {
  @Test
  public void type_of_bool_is_bool_type() throws Exception {
    assertThat(boolB(true).category()).isEqualTo(boolTB());
  }

  @Test
  public void to_j_returns_java_true_from_true_bool() throws Exception {
    BoolB bool = boolB(true);
    assertThat(bool.toJ()).isTrue();
  }

  @Test
  public void javlue_returns_java_false_from_false_bool() throws Exception {
    BoolB bool = boolB(false);
    assertThat(bool.toJ()).isFalse();
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<BoolB> {
    @Override
    protected List<BoolB> equalExprs() throws BytecodeException {
      return list(boolB(true), boolB(true));
    }

    @Override
    protected List<BoolB> nonEqualExprs() throws BytecodeException {
      return list(boolB(true), boolB(false));
    }
  }

  @Test
  public void bool_can_be_read_back_by_hash() throws Exception {
    BoolB bool = boolB(true);
    Hash hash = bool.hash();
    assertThat(exprDbOther().get(hash)).isEqualTo(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_to_j() throws Exception {
    BoolB bool = boolB(true);
    assertThat(((BoolB) exprDbOther().get(bool.hash())).toJ()).isTrue();
  }

  @Test
  public void to_string_contains_value() throws Exception {
    BoolB bool = boolB(true);
    assertThat(bool.toString()).isEqualTo("true@" + bool.hash());
  }
}

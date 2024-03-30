package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf.BSubExprs;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BIfTest extends TestingVirtualMachine {
  @Test
  void creating_if_with_non_bool_condition_fails() {
    assertCall(() -> bIf(bInt(), bInt(), bInt()))
        .throwsException(new IllegalArgumentException(
            "`condition.evaluationType()` should be `Bool` but is `Int`."));
  }

  @Test
  void creating_if_with_then_and_else_having_different_evaluation_type_fails() {
    assertCall(() -> bIf(bBool(), bInt(), bString()))
        .throwsException(new IllegalArgumentException(
            "`then.evaluationType()` should be `String` but is `Int`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BIf> {
    @Override
    protected List<BIf> equalExprs() throws BytecodeException {
      return list(bIf(bBool(true), bInt(1), bInt(2)), bIf(bBool(true), bInt(1), bInt(2)));
    }

    @Override
    protected List<BIf> nonEqualExprs() throws BytecodeException {
      return list(
          bIf(bBool(true), bInt(1), bInt(2)),
          bIf(bBool(false), bInt(1), bInt(3)),
          bIf(bBool(true), bInt(1), bInt(3)),
          bIf(bBool(true), bInt(3), bInt(2)),
          bIf(bBool(true), bInt(2), bInt(1)));
    }
  }

  @Test
  public void if_can_be_read_back_by_hash() throws Exception {
    var ifB = bIf(bBool(true), bInt(1), bInt(2));
    assertThat(exprDbOther().get(ifB.hash())).isEqualTo(ifB);
  }

  @Test
  public void if_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var condition = bBool(true);
    var then_ = bInt(1);
    var else_ = bInt(2);
    var ifB = bIf(condition, then_, else_);
    assertThat(((BIf) exprDbOther().get(ifB.hash())).subExprs())
        .isEqualTo(new BSubExprs(condition, then_, else_));
  }

  @Test
  public void to_string() throws Exception {
    var ifB = bIf(bBool(true), bInt(1), bInt(2));
    assertThat(ifB.toString()).isEqualTo("IF:Int(???)@" + ifB.hash());
  }
}

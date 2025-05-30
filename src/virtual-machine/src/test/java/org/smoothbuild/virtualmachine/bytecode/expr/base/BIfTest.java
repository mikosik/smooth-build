package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BIfTest extends VmTestContext {
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
  void if_can_be_read_back_by_hash() throws Exception {
    var ifB = bIf(bBool(true), bInt(1), bInt(2));
    assertThat(exprDbOther().get(ifB.hash())).isEqualTo(ifB);
  }

  @Test
  void if_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var condition = bBool(true);
    var then_ = bInt(1);
    var else_ = bInt(2);
    var ifB = bIf(condition, then_, else_);
    assertThat(((BIf) exprDbOther().get(ifB.hash())).subExprs())
        .isEqualTo(new BSubExprs(condition, then_, else_));
  }

  @Test
  void to_string() throws Exception {
    var ifB = bIf(bBool(true), bInt(1), bInt(2));
    assertThat(ifB.toString())
        .isEqualTo(
            """
        BIf(
          hash = ae2abb3bad2420d56b3777571d60d2b9086d8153d22d332e7da71918e7f830de
          evaluationType = Int
          condition = BBool(
            hash = e9585a54d9f08cc32a4c31683378c0fdc64e7b8fb6af4eb92ba3c9cf8911e8ba
            type = Bool
            value = true
          )
          then = BInt(
            hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
            type = Int
            value = 1
          )
          else = BInt(
            hash = d12e6fd8b83ab61eb4936e0924c6c6c682b6ee3a08148533efff2e72fcfef8c8
            type = Int
            value = 2
          )
        )""");
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BCombineTest extends TestingVirtualMachine {
  @Test
  void kind_returns_kind() throws Exception {
    var combine = bCombine(bInt(3));
    assertThat(combine.kind()).isEqualTo(bCombineKind(bIntType()));
  }

  @Test
  void items_returns_items() throws Exception {
    assertThat(bCombine(bInt(1), bString("abc")).items()).isEqualTo(list(bInt(1), bString("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCombine> {
    @Override
    protected java.util.List<BCombine> equalExprs() throws BytecodeException {
      return list(bCombine(bInt(1), bString("abc")), bCombine(bInt(1), bString("abc")));
    }

    @Override
    protected java.util.List<BCombine> nonEqualExprs() throws BytecodeException {
      return list(
          bCombine(bInt(1)),
          bCombine(bInt(2)),
          bCombine(bString("abc")),
          bCombine(bInt(1), bString("abc")));
    }
  }

  @Test
  void combine_can_be_read_back_by_hash() throws Exception {
    var combine = bCombine(bInt(1));
    assertThat(exprDbOther().get(combine.hash())).isEqualTo(combine);
  }

  @Test
  void combine_read_back_by_hash_has_same_items() throws Exception {
    var combine = bCombine(bInt(), bString());
    assertThat(((BCombine) exprDbOther().get(combine.hash())).items())
        .isEqualTo(list(bInt(), bString()));
  }

  @Test
  void to_string() throws Exception {
    var combine = bCombine(bInt(1));
    assertThat(combine.toString()).isEqualTo("COMBINE:{Int}(???)@" + combine.hash());
  }
}

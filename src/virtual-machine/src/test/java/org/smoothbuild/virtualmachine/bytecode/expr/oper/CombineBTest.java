package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class CombineBTest extends TestVirtualMachine {
  @Test
  public void category_returns_category() throws Exception {
    var combineH = combineB(intB(3));
    assertThat(combineH.category()).isEqualTo(combineCB(intTB()));
  }

  @Test
  public void items_returns_items() throws Exception {
    assertThat(combineB(intB(1), stringB("abc")).items()).isEqualTo(list(intB(1), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<CombineB> {
    @Override
    protected java.util.List<CombineB> equalExprs() throws BytecodeException {
      return list(combineB(intB(1), stringB("abc")), combineB(intB(1), stringB("abc")));
    }

    @Override
    protected java.util.List<CombineB> nonEqualExprs() throws BytecodeException {
      return list(
          combineB(intB(1)),
          combineB(intB(2)),
          combineB(stringB("abc")),
          combineB(intB(1), stringB("abc")));
    }
  }

  @Test
  public void combine_can_be_read_back_by_hash() throws Exception {
    CombineB combine = combineB(intB(1));
    assertThat(exprDbOther().get(combine.hash())).isEqualTo(combine);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() throws Exception {
    var combine = combineB(intB(), stringB());
    assertThat(((CombineB) exprDbOther().get(combine.hash())).items())
        .isEqualTo(list(intB(), stringB()));
  }

  @Test
  public void to_string() throws Exception {
    CombineB combine = combineB(intB(1));
    assertThat(combine.toString()).isEqualTo("COMBINE:{Int}(???)@" + combine.hash());
  }
}

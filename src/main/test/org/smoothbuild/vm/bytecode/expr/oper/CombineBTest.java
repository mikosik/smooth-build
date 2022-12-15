package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

import com.google.common.truth.Truth;

public class CombineBTest extends TestContext {
  @Test
  public void category_returns_category() {
    var combineH = combineB(intB(3));
    Truth.assertThat(combineH.category())
        .isEqualTo(combineCB(intTB()));
  }

  @Test
  public void items_returns_items() {
    Truth.assertThat(combineB(intB(1), stringB("abc")).dataSeq())
        .isEqualTo(list(intB(1), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<CombineB> {
    @Override
    protected List<CombineB> equalExprs() {
      return list(
          combineB(intB(1), stringB("abc")),
          combineB(intB(1), stringB("abc"))
      );
    }

    @Override
    protected List<CombineB> nonEqualExprs() {
      return list(
          combineB(intB(1)),
          combineB(intB(2)),
          combineB(stringB("abc")),
          combineB(intB(1), stringB("abc"))
      );
    }
  }

  @Test
  public void combine_can_be_read_back_by_hash() {
    CombineB combine = combineB(intB(1));
    Truth.assertThat(bytecodeDbOther().get(combine.hash()))
        .isEqualTo(combine);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    CombineB combine = combineB(intB(), stringB());
    assertThat(((CombineB) bytecodeDbOther().get(combine.hash())).dataSeq())
        .isEqualTo(list(intB(), stringB()));
  }

  @Test
  public void to_string() {
    CombineB combine = combineB(intB(1));
    assertThat(combine.toString())
        .isEqualTo("COMBINE:{Int}(???)@" + combine.hash());
  }
}

package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;

import io.vavr.collection.Array;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class CombineBTest extends TestContext {
  @Test
  public void category_returns_category() {
    var combineH = combineB(intB(3));
    assertThat(combineH.category()).isEqualTo(combineCB(intTB()));
  }

  @Test
  public void items_returns_items() {
    assertThat(combineB(intB(1), stringB("abc")).items())
        .isEqualTo(Array.of(intB(1), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<CombineB> {
    @Override
    protected List<CombineB> equalExprs() {
      return list(combineB(intB(1), stringB("abc")), combineB(intB(1), stringB("abc")));
    }

    @Override
    protected List<CombineB> nonEqualExprs() {
      return list(
          combineB(intB(1)),
          combineB(intB(2)),
          combineB(stringB("abc")),
          combineB(intB(1), stringB("abc")));
    }
  }

  @Test
  public void combine_can_be_read_back_by_hash() {
    CombineB combine = combineB(intB(1));
    assertThat(bytecodeDbOther().get(combine.hash())).isEqualTo(combine);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    var combine = combineB(intB(), stringB());
    assertThat(((CombineB) bytecodeDbOther().get(combine.hash())).items())
        .isEqualTo(Array.of(intB(), stringB()));
  }

  @Test
  public void to_string() {
    CombineB combine = combineB(intB(1));
    assertThat(combine.toString()).isEqualTo("COMBINE:{Int}(???)@" + combine.hash());
  }
}

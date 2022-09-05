package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class CombineBTest extends TestContext {
  @Test
  public void cat_returns_category() {
    var combineH = combineB(tupleTB(intTB()), intB(3));
    assertThat(combineH.cat())
        .isEqualTo(combineCB(intTB()));
  }

  @Test
  public void item_not_matching_type_specified_in_category_causes_exc() {
    assertCall(() -> combineB(tupleTB(intTB()), stringB()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void item_size_greater_than_type_items_size_causes_exc() {
    assertCall(() -> combineB(tupleTB(intTB()), intB(), intB()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void item_size_lower_than_type_items_size_causes_exc() {
    assertCall(() -> combineB(tupleTB(intTB())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void items_returns_items() {
    assertThat(combineB(intB(1), stringB("abc")).items())
        .isEqualTo(list(intB(1), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<CombineB> {
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
    assertThat(bytecodeDbOther().get(combine.hash()))
        .isEqualTo(combine);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    CombineB combine = combineB(intB(), stringB());
    assertThat(((CombineB) bytecodeDbOther().get(combine.hash())).items())
        .isEqualTo(list(intB(), stringB()));
  }

  @Test
  public void to_string() {
    CombineB combine = combineB(intB(1));
    assertThat(combine.toString())
        .isEqualTo("Combine:{Int}(???)@" + combine.hash());
  }
}

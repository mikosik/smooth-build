package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class CombineBTest extends TestingContext {
  @Test
  public void cat_returns_category() {
    var combineH = combineB(tupleTB(intTB()), intB(3));
    assertThat(combineH.cat())
        .isEqualTo(combineCB(intTB()));
  }

  @Test
  public void creating_combine_with_resT_having_open_vars_causes_exc() {
    var a = oVarTB("A");
    assertCall(() -> combineB(tupleTB(a)))
        .throwsException(new IllegalArgumentException("evalT must not have open vars"));
  }

  @Test
  public void item_not_matching_type_specified_in_category_causes_exc() {
    assertCall(() -> combineB(tupleTB(intTB()), stringB()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void item_can_be_subtype_of_item_type_specified_in_category() {
    var elemT = arrayB(nothingTB());
    var combineB = combineB(tupleTB(arrayTB(intTB())), elemT);
    assertThat(combineB.items().get(0))
        .isEqualTo(elemT);
  }

  @Test
  public void item_matching_polytype_specified_in_category() {
    var varA = cVarTB("A");
    combineB(tupleTB(varA), paramRefB(varA, 0));
  }

  @Test
  public void items_returns_items() {
    assertThat(combineB(intB(1), stringB("abc")).items())
        .isEqualTo(list(intB(1), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<CombineB> {
    @Override
    protected List<CombineB> equalValues() {
      return list(
          combineB(intB(1), stringB("abc")),
          combineB(intB(1), stringB("abc"))
      );
    }

    @Override
    protected List<CombineB> nonEqualValues() {
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
    CombineB expr = combineB(intB(1));
    assertThat(objDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    CombineB expr = combineB(intB(), stringB());
    assertThat(((CombineB) objDbOther().get(expr.hash())).items())
        .isEqualTo(list(intB(), stringB()));
  }

  @Test
  public void to_string() {
    CombineB expr = combineB(intB(1));
    assertThat(expr.toString())
        .isEqualTo("Combine:{Int}(???)@" + expr.hash());
  }
}

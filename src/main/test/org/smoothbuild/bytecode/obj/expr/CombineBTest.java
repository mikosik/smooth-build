package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CombineBTest extends TestingContext {
  @Test
  public void cat_returns_category() {
    var combineH = combineB(tupleTB(list(intTB())), list(intB(3)));
    assertThat(combineH.cat())
        .isEqualTo(combineCB(list(intTB())));
  }

  @Test
  public void item_not_matching_type_specified_in_category_causes_exc() {
    assertCall(() -> combineB(tupleTB(list(intTB())), list(stringB())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void item_can_be_subtype_of_item_type_specified_in_category() {
    var elemT = arrayB(nothingTB());
    var combineB = combineB(tupleTB(list(arrayTB(intTB()))), list(elemT));
    assertThat(combineB.items().get(0))
        .isEqualTo(elemT);
  }

  @Test
  public void item_matching_polytype_specified_in_category() {
    var varA = varTB("A");
    combineB(tupleTB(list(varA)), list(paramRefB(varA, 0)));
  }

  @Test
  public void items_returns_items() {
    ImmutableList<ObjB> items = list(intB(1), stringB("abc"));
    assertThat(combineB(items).items())
        .isEqualTo(items);
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<CombineB> {
    @Override
    protected List<CombineB> equalValues() {
      return list(
          combineB(list(intB(1), stringB("abc"))),
          combineB(list(intB(1), stringB("abc")))
      );
    }

    @Override
    protected List<CombineB> nonEqualValues() {
      return list(
          combineB(list(intB(1))),
          combineB(list(intB(2))),
          combineB(list(stringB("abc"))),
          combineB(list(intB(1), stringB("abc")))
      );
    }
  }

  @Test
  public void combine_can_be_read_back_by_hash() {
    CombineB expr = combineB(list(intB(1)));
    assertThat(byteDbOther().get(expr.hash()))
        .isEqualTo(expr);
  }

  @Test
  public void combine_read_back_by_hash_has_same_items() {
    ImmutableList<ObjB> items = list(intB(), stringB());
    CombineB expr = combineB(items);
    assertThat(((CombineB) byteDbOther().get(expr.hash())).items())
        .isEqualTo(items);
  }

  @Test
  public void to_string() {
    CombineB expr = combineB(list(intB(1)));
    assertThat(expr.toString())
        .isEqualTo("Combine:{Int}(???)@" + expr.hash());
  }
}

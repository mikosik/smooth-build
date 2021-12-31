package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Lists;

public class SelectBTest extends TestingContext {
  @Test
  public void selected_fieldT_can_be_subtype_of_evalT() {
    var select = selectB(arrayTB(intTB()), tupleB(list(arrayB(nothingTB()))), intB(0));
    select.data();
  }

  @Test
  public void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> selectB(boolTB(), intB(3), intB(2)))
        .throwsException(new IllegalArgumentException(
            "Selectable.type() should be instance of TupleTB but is `Int`"));
  }

  @Test
  public void creating_select_with_too_great_index_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(boolTB(), tuple, intB(2)).cat())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  public void creating_select_with_index_lower_than_zero_causes_exception() {
    TupleB tuple = animalB("rabbit", 7);
    assertCall(() -> selectB(stringTB(), tuple, intB(-1)).cat())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  public void tuple_itemT_can_be_subtype_of_elemT_specified_in_category() {
    var combine = combineB(tupleTB(list(arrayTB(nothingTB()))), Lists.list(arrayB(nothingTB())));
    var select = selectB(arrayTB(intTB()), combine, intB(0));
    assertThat(select.data().selectable())
        .isEqualTo(combine);
  }

  @Test
  public void tuple_itemT_not_being_subtype_of_elemT_specified_in_category_causes_exc() {
    var tuple = tupleB(list(intB(7)));
    assertCall(() -> selectB(stringTB(), tuple, intB(0)))
        .throwsException(new IllegalArgumentException(
            "Selected item type `Int` cannot be assigned to evalT `String`."));
  }

  @Test
  public void data_returns_tuple_and_index() {
    TupleB selectable = tupleB(tupleTB(), list(intB(7)));
    IntB index = intB(0);
    assertThat(selectB(selectable, index).data())
        .isEqualTo(new SelectB.Data(selectable, index));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<SelectB> {
    @Override
    protected List<SelectB> equalValues() {
      return list(
          selectB(tupleB(list(intB(7), stringB("abc"))), intB(0)),
          selectB(tupleB(list(intB(7), stringB("abc"))), intB(0))
      );
    }

    @Override
    protected List<SelectB> nonEqualValues() {
      return list(
          selectB(tupleB(list(intB(1))), intB(0)),
          selectB(tupleB(list(intB(2))), intB(0)),
          selectB(tupleB(list(intB(2), intB(2))), intB(0)),
          selectB(tupleB(list(intB(2), intB(2))), intB(1)),
          selectB(tupleB(list(intB(2), intB(7))), intB(0)),
          selectB(tupleB(list(intB(7), intB(2))), intB(0))
      );
    }
  }

  @Test
  public void select_can_be_read_back_by_hash() {
    TupleB tuple = animalB("rabbit", 7);
    SelectB select = selectB(tuple, intB(0));
    assertThat(byteDbOther().get(select.hash()))
        .isEqualTo(select);
  }

  @Test
  public void select_read_back_by_hash_has_same_data() {
    var selectable = animalB();
    var index = intB(0);
    var select = selectB(selectable, index);
    assertThat(((SelectB) byteDbOther().get(select.hash())).data())
        .isEqualTo(new SelectB.Data(selectable, index));
  }

  @Test
  public void to_string() {
    SelectB select = selectB(animalB(), intB(0));
    assertThat(select.toString())
        .isEqualTo("Select:String(???)@" + select.hash());
  }
}

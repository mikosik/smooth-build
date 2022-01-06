package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class PickBTest extends TestingContext {
  @Test
  public void picked_elemT_can_be_subtype_of_evalT() {
    var pick = pickB(arrayTB(intTB()), arrayB(arrayB(nothingTB())), intB(0));
    pick.data();
  }

  @Test
  public void creating_pick_with_non_array_expr_causes_exception() {
    assertCall(() -> pickB(intTB(), intB(3), intB(2)))
        .throwsException(new IllegalArgumentException(
            "pickable.type() should be instance of ArrayTB but is `Int`."));
  }

  @Test
  public void creating_pick_with_non_int_index_expr_causes_exception() {
    assertCall(() -> pickB(intTB(), arrayB(intB(3)), stringB()))
        .throwsException(new IllegalArgumentException("Index type is `String` but must be `Int`."));
  }

  @Test
  public void data_returns_array_and_index() {
    var array = arrayB(intB(7));
    var index = intB(0);
    assertThat(pickB(array, index).data())
        .isEqualTo(new PickB.Data(array, index));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<PickB> {
    @Override
    protected List<PickB> equalValues() {
      return list(
          pickB(arrayB(intB(7)), intB(0)),
          pickB(arrayB(intB(7)), intB(0))
      );
    }

    @Override
    protected List<PickB> nonEqualValues() {
      return list(
          pickB(arrayB(intB(7)), intB(0)),
          pickB(arrayB(intB(7)), intB(1)),
          pickB(arrayB(intB(8)), intB(0))
      );
    }
  }

  @Test
  public void pick_can_be_read_back_by_hash() {
    var pick = pickB(arrayB(intB(7)), intB(0));
    assertThat(byteDbOther().get(pick.hash()))
        .isEqualTo(pick);
  }

  @Test
  public void pick_read_back_by_hash_has_same_data() {
    var array = arrayB(intB(7));
    var index = intB(0);
    var pick = pickB(array, index);
    assertThat(((PickB) byteDbOther().get(pick.hash())).data())
        .isEqualTo(new PickB.Data(array, index));
  }

  @Test
  public void to_string() {
    var pick = pickB(arrayB(intB(7)), intB(0));
    assertThat(pick.toString())
        .isEqualTo("Pick:Int(???)@" + pick.hash());
  }
}

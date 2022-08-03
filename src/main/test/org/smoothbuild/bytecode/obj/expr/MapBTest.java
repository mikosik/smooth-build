package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestContext;

public class MapBTest extends TestContext {
  @Nested
  class _inferring_type {
    @Test
    public void simple_case() {
      var mapB = mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc")));
      assertThat(mapB.type())
          .isEqualTo(arrayTB(stringTB()));
    }
  }

  @Test
  public void array_evaluating_to_not_array_causes_exc() {
    assertCall(() -> mapB(intB(), funcB(list(intTB()), stringB("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void func_evaluating_to_not_function_causes_exc() {
    assertCall(() -> mapB(arrayB(intB(7)), intB()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void func_evaluating_to_func_with_not_one_param_causes_exc() {
    assertCall(() -> mapB(arrayB(intB()), funcB(list(intTB(), intTB()), stringB("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_elem_not_assignable_to_mapping_func_param_causes_exc() {
    assertCall(() -> mapB(arrayB(boolB()), funcB(list(intTB()), stringB("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_getter() {
    var mapB = mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc")));
    assertThat(mapB.data().array())
        .isEqualTo(arrayB(intB(7)));
  }

  @Test
  public void func_getter() {
    var mapB = mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc")));
    assertThat(mapB.data().func())
        .isEqualTo(funcB(list(intTB()), stringB("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<MapB> {
    @Override
    protected List<MapB> equalValues() {
      return list(
          mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc"))),
          mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc")))
      );
    }

    @Override
    protected List<MapB> nonEqualValues() {
      return list(
          mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc"))),
          mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("def"))),
          mapB(arrayB(blobB(3)), funcB(list(blobTB()), stringB("abc"))),
          mapB(arrayB(intTB()), funcB(list(intTB()), stringB("abc")))
      );
    }
  }

  @Test
  public void map_can_be_read_back_by_hash() {
    var mapB = mapB(arrayB(intB(7)), funcB(list(intTB()), stringB("abc")));
    assertThat(objDbOther().get(mapB.hash()))
        .isEqualTo(mapB);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var array = arrayB(intB(7));
    var func = funcB(list(intTB()), stringB("abc"));
    var mapB = mapB(array, func);

    var readMap = (MapB) objDbOther().get(mapB.hash());
    var readMapData = readMap.data();
    var mapData = mapB.data();

    assertThat(readMapData.array())
        .isEqualTo(mapData.array());
    assertThat(readMapData.func())
        .isEqualTo(mapData.func());
  }

  @Test
  public void to_string() {
    var array = arrayB(intB(7));
    var func = funcB(list(intTB()), stringB("abc"));
    var mapB = mapB(array, func);

    assertThat(mapB.toString())
        .isEqualTo("Map:[String](???)@" + mapB.hash());
  }
}

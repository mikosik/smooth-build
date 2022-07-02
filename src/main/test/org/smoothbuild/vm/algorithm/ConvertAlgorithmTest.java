package org.smoothbuild.vm.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.testing.TestContext;

public class ConvertAlgorithmTest extends TestContext {
  @Nested
  class _unnecessary_conversions {
    @Test
    public void converting_string_to_string_fails() {
      assertConvertThrowsIllegalArgumentExc(stringTB(), stringB("abc"));
    }

    @Test
    public void converting_int_to_int_fails() {
      assertConvertThrowsIllegalArgumentExc(intTB(), intB(3));
    }

    @Test
    public void converting_bool_to_bool_fails() {
      assertConvertThrowsIllegalArgumentExc(boolTB(), boolB(true));
    }

    @Test
    public void converting_blob_to_blob_fails() {
      assertConvertThrowsIllegalArgumentExc(blobTB(), blobB());
    }

    @Test
    public void converting_array_to_array_of_same_type_fails() {
      assertConvertThrowsIllegalArgumentExc(arrayTB(intTB()), arrayB(intB(3)));
    }

    @Test
    public void converting_tuple_to_tuple_of_same_type_fails() {
      assertConvertThrowsIllegalArgumentExc(tupleTB(intTB()), tupleB(intB(3)));
    }

    private void assertConvertThrowsIllegalArgumentExc(TypeB typeB, CnstB cnst) {
      assertConvertThrowsExc(typeB, cnst, IllegalArgumentException.class);
    }
  }

  @Nested
  class _illegal_conversions {
    @Test
    public void array_type_to_base_type() {
      assertConvertThrowsRuntimeExc(intTB(), arrayB(intB(3)));
    }

    @Test
    public void array_type_to_func_type() {
      assertConvertThrowsClassCastExc(funcTB(intTB(), list()), arrayB(intB(3)));
    }

    @Test
    public void array_type_to_tuple_type() {
      assertConvertThrowsClassCastExc(tupleTB(intTB()), arrayB(intB(3)));
    }

    @Test
    public void array_type_to_inconvertible_array_type() {
      assertConvertThrowsRuntimeExc(arrayTB(intTB()), arrayB(stringB("abc")));
    }

    @Test
    public void base_type_to_other_base_type() {
      assertConvertThrowsRuntimeExc(stringTB(), intB(3));
    }

    @Test
    public void base_type_to_array_type() {
      assertConvertThrowsClassCastExc(arrayTB(intTB()), intB(3));
    }

    @Test
    public void base_type_to_func_type() {
      assertConvertThrowsClassCastExc(funcTB(intTB(), list()), intB(3));
    }

    @Test
    public void base_type_to_tuple_type() {
      assertConvertThrowsClassCastExc(tupleTB(intTB()), intB(3));
    }

    @Test
    public void func_type_to_other_func_type_with_different_resT() {
      assertConvertThrowsIllegalArgExc(funcTB(intTB(), list()), funcB(stringB()));
    }

    @Test
    public void func_type_to_other_func_type_with_different_params_size() {
      assertConvertThrowsIllegalArgExc(
          funcTB(intTB(), list(intTB())), funcB(list(intTB(), intTB()), intB()));
    }

    @Test
    public void func_type_to_other_func_type_with_different_paramT() {
      assertConvertThrowsIllegalArgExc(
          funcTB(intTB(), list(stringTB())), funcB(list(boolTB()), intB()));
    }

    @Test
    public void func_type_to_array_type() {
      assertConvertThrowsClassCastExc(arrayTB(intTB()), funcB(intB()));
    }

    @Test
    public void func_type_to_tuple_type() {
      assertConvertThrowsClassCastExc(tupleTB(intTB()), funcB(intB()));
    }

    @Test
    public void tuple_type_to_base_type() {
      assertConvertThrowsRuntimeExc(intTB(), tupleB(intB(3)));
    }

    @Test
    public void tuple_type_to_array_type() {
      assertConvertThrowsClassCastExc(arrayTB(intTB()), tupleB(intB(3)));
    }

    @Test
    public void tuple_type_to_func_type() {
      assertConvertThrowsClassCastExc(funcTB(intTB(), list()), tupleB(intB(3)));
    }

    @Test
    public void tuple_type_to_inconvertible_tuple_type() {
      assertConvertThrowsRuntimeExc(tupleTB(stringTB()), tupleB(intB(3)));
    }
  }

  @Nested
  class _array {
    @Test
    public void converting_array_level_1_to_1() {
      var targetT = arrayTB(stringTB());
      var val = arrayB(nothingTB());
      var expected = arrayB(stringTB());
      assertThat(convert(targetT, val))
          .isEqualTo(expected);
    }

    @Test
    public void converting_array_two_levels_deep_level_2_to_2() {
      var targetT = arrayTB(arrayTB(stringTB()));
      var val = arrayB(arrayB(nothingTB()));
      var expected = arrayB(arrayB(stringTB()));
      assertConversion(targetT, val, expected);
    }

    @Test
    public void converting_array_three_levels_deep_level_3_to_3() {
      var targetT = arrayTB(arrayTB(arrayTB(stringTB())));
      var val = arrayB(arrayB(arrayB(nothingTB())));
      var expected = arrayB(arrayB(arrayB(stringTB())));
      assertConversion(targetT, val, expected);
    }

    @Test
    public void converting_array_three_levels_deep_level_1_to_3() {
      var targetT = arrayTB(arrayTB(arrayTB(stringTB())));
      var val = arrayB(nothingTB());
      var expected = arrayB(arrayTB(arrayTB(stringTB())));
      assertConversion(targetT, val, expected);
    }
  }

  @Nested
  class _func {
    @Test
    public void converting_func_resT_and_its_body() {
      var targetT = funcTB(arrayTB(intTB()), list());
      var val = funcB(funcTB(arrayTB(nothingTB()), list()), arrayB(nothingTB()));
      var expected = funcB(funcTB(arrayTB(intTB()), list()), arrayB(intTB()));
      assertConversion(targetT, val, expected);
    }

    @Test
    public void converting_func_paramT() {
      var targetT = funcTB(intTB(), list(arrayTB(nothingTB())));
      var val = funcB(funcTB(intTB(), list(arrayTB(intTB()))), intB());
      var expected = funcB(funcTB(intTB(), list(arrayTB(nothingTB()))), intB());
      assertConversion(targetT, val, expected);
    }
  }

  @Nested
  class _tuple {
    @Test
    public void converting_tuple() {
      var targetT = tupleTB(arrayTB(stringTB()));
      var val = tupleB(arrayB(nothingTB()));
      var expected = tupleB(arrayB(stringTB()));
      assertConversion(targetT, val, expected);
    }

    @Test
    public void converting_tuple_two_levels_deep() {
      var targetT = tupleTB(tupleTB(arrayTB(stringTB())));
      var val = tupleB(tupleB(arrayB(nothingTB())));
      var expected = tupleB(tupleB(arrayB(stringTB())));
      assertConversion(targetT, val, expected);
    }
  }

  private void assertConvertThrowsRuntimeExc(TypeB typeB, CnstB cnst) {
    assertConvertThrowsExc(typeB, cnst, RuntimeException.class);
  }

  private void assertConvertThrowsIllegalArgExc(TypeB typeB, CnstB cnst) {
    assertConvertThrowsExc(typeB, cnst, IllegalArgumentException.class);
  }

  private void assertConvertThrowsClassCastExc(TypeB typeB, CnstB cnst) {
    assertConvertThrowsExc(typeB, cnst, ClassCastException.class);
  }

  private void assertConvertThrowsExc(TypeB typeB, CnstB cnst, Class<? extends Throwable> expected) {
    var convertAlgorithm = new ConvertAlgorithm(typeB);
    assertCall(() -> convertAlgorithm.run(input(cnst), newNativeApi()))
        .throwsException(expected);
  }

  private void assertConversion(TypeB targetT, CnstB cnst, CnstB expected) {
    assertThat(convert(targetT, cnst))
        .isEqualTo(expected);
  }

  private CnstB convert(TypeB targetT, CnstB input) {
    var output = new ConvertAlgorithm(targetT)
        .run(input(input), newNativeApi());
    return output.cnst();
  }

  private TupleB input(CnstB cnstH) {
    return tupleB(cnstH);
  }
}

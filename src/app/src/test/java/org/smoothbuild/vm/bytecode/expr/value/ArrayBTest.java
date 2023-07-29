package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import okio.ByteString;

public class ArrayBTest extends TestContext {
  @Test
  public void empty_int_array_can_be_iterated_as_int() {
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB(intTB()))
        .build();
    assertThat(array.elems(IntB.class))
        .isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() {
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB(stringTB()))
        .add(stringB("abc"))
        .build();
    assertCall(() -> array.elems(TupleB.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + TupleB.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() {
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .build();
    assertThat(array.elems(StringB.class))
        .isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() {
    ArrayBBuilder arrayBuilder = bytecodeDb().arrayBuilder(arrayTB());
    assertCall(() -> arrayBuilder.add(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_elem_with_wrong_type_is_forbidden() {
    ArrayBBuilder arrayBuilder = bytecodeDb().arrayBuilder(arrayTB());
    assertCall(() -> arrayBuilder.add(blobB(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_elem() {
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(stringB("abc"))
        .build();
    assertThat(array.elems(StringB.class))
        .containsExactly(stringB("abc"));
  }

  @Test
  public void array_contains_added_elem_via_add_all_method() {
    StringB str = stringB("abc");
    StringB str2 = stringB("def");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .addAll(list(str, str2))
        .build();
    assertThat(array.elems(StringB.class))
        .containsExactly(str, str2)
        .inOrder();
  }

  @Test
  public void array_contains_added_elems_in_order() {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    StringB str3 = stringB("ghi");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str1)
        .add(str2)
        .add(str3)
        .build();
    assertThat(array.elems(StringB.class))
        .containsExactly(str1, str2, str3)
        .inOrder();
  }

  @Test
  public void adding_same_elem_twice_builds_array_with_two_elems() {
    StringB str = stringB("abc");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str)
        .add(str)
        .build();
    assertThat(array.elems(StringB.class))
        .containsExactly(str, str);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ArrayB> {
    @Override
    protected List<ArrayB> equalExprs() {
      return list(
          arrayB(intB(0), intB(1)),
          arrayB(intB(0), intB(1))
      );
    }

    @Override
    protected List<ArrayB> nonEqualExprs() {
      return list(
          arrayB(intTB()),
          arrayB(stringTB()),
          arrayB(intB(0)),
          arrayB(intB(1)),
          arrayB(intB(0), intB(1))
      );
    }
  }

  @Test
  public void array_can_be_read_by_hash() {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str1)
        .add(str2)
        .build();
    assertThat(bytecodeDbOther().get(array.hash()))
        .isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elems() {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str1)
        .add(str2)
        .build();
    assertThat(((ArrayB) bytecodeDbOther().get(array.hash())).elems(StringB.class))
        .containsExactly(str1, str2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str1)
        .add(str2)
        .build();
    assertThat(bytecodeDbOther().get(array.hash()).hash())
        .isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(TypeB elemT) {
    var arrayTH = arrayTB(elemT);
    var arrayH = bytecodeDb().arrayBuilder(arrayTH).build();
    assertThat(arrayH.category())
        .isEqualTo(arrayTH);
  }

  private static List<CategoryB> type_test_data() {
    return TestingCatsB.CATS_TO_TEST;
  }

  @Test
  public void to_string() {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    ArrayB array = bytecodeDb().arrayBuilder(arrayTB())
        .add(str1)
        .add(str2)
        .build();
    assertThat(array.toString())
        .isEqualTo("""
            ["abc","def"]@""" + array.hash());
  }
}

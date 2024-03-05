package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.testing.TestingCategoryB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ArrayBTest extends TestingVirtualMachine {
  @Test
  public void empty_int_array_can_be_iterated_as_int() throws Exception {
    var array = exprDb().newArrayBuilder(arrayTB(intTB())).build();
    assertThat(array.elements(IntB.class)).isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() throws Exception {
    var array =
        exprDb().newArrayBuilder(arrayTB(stringTB())).add(stringB("abc")).build();
    assertCall(() -> array.elements(TupleB.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + TupleB.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    var array = exprDb().newArrayBuilder(arrayTB()).build();
    assertThat(array.elements(StringB.class)).isEmpty();
  }

  @Test
  public void adding_null_is_forbidden() throws Exception {
    var arrayBuilder = exprDb().newArrayBuilder(arrayTB());
    assertCall(() -> arrayBuilder.add(null)).throwsException(NullPointerException.class);
  }

  @Test
  public void adding_elem_with_wrong_type_is_forbidden() throws Exception {
    var arrayBuilder = exprDb().newArrayBuilder(arrayTB());
    assertCall(() -> arrayBuilder.add(blobB(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void array_contains_added_elem() throws Exception {
    var array = exprDb().newArrayBuilder(arrayTB()).add(stringB("abc")).build();
    assertThat(array.elements(StringB.class)).containsExactly(stringB("abc"));
  }

  @Test
  public void array_contains_added_elem_via_add_all_method() throws Exception {
    StringB str = stringB("abc");
    StringB str2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).addAll(list(str, str2)).build();
    assertThat(array.elements(StringB.class)).containsExactly(str, str2).inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() throws Exception {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    StringB str3 = stringB("ghi");
    var array =
        exprDb().newArrayBuilder(arrayTB()).add(str1).add(str2).add(str3).build();
    assertThat(array.elements(StringB.class)).containsExactly(str1, str2, str3).inOrder();
  }

  @Test
  public void adding_same_elem_twice_builds_array_with_two_elements() throws Exception {
    StringB str = stringB("abc");
    var array = exprDb().newArrayBuilder(arrayTB()).add(str).add(str).build();
    assertThat(array.elements(StringB.class)).containsExactly(str, str);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ArrayB> {
    @Override
    protected List<ArrayB> equalExprs() throws BytecodeException {
      return list(arrayB(intB(0), intB(1)), arrayB(intB(0), intB(1)));
    }

    @Override
    protected List<ArrayB> nonEqualExprs() throws BytecodeException {
      return list(
          arrayB(intTB()),
          arrayB(stringTB()),
          arrayB(intB(0)),
          arrayB(intB(1)),
          arrayB(intB(0), intB(1)));
    }
  }

  @Test
  public void array_can_be_read_by_hash() throws Exception {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(str1).add(str2).build();
    assertThat(exprDbOther().get(array.hash())).isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() throws Exception {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(str1).add(str2).build();
    assertThat(((ArrayB) exprDbOther().get(array.hash())).elements(StringB.class))
        .containsExactly(str1, str2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() throws Exception {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(str1).add(str2).build();
    assertThat(exprDbOther().get(array.hash()).hash()).isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(TypeB elemT) throws Exception {
    var arrayTH = arrayTB(elemT);
    var arrayH = exprDb().newArrayBuilder(arrayTH).build();
    assertThat(arrayH.category()).isEqualTo(arrayTH);
  }

  private static List<CategoryB> type_test_data() {
    return TestingCategoryB.CATS_TO_TEST;
  }

  @Test
  public void to_string() throws Exception {
    StringB str1 = stringB("abc");
    StringB str2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(str1).add(str2).build();
    assertThat(array.toString()).isEqualTo("""
            ["abc","def"]@""" + array.hash());
  }
}

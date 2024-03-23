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
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.testing.TestingCategoryB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BArrayTest extends TestingVirtualMachine {
  @Test
  public void empty_int_array_can_be_iterated_as_int() throws Exception {
    var array = exprDb().newArrayBuilder(arrayTB(intTB())).build();
    assertThat(array.elements(BInt.class)).isEmpty();
  }

  @Test
  public void string_array_cannot_be_iterated_as_tuple() throws Exception {
    var array =
        exprDb().newArrayBuilder(arrayTB(stringTB())).add(stringB("abc")).build();
    assertCall(() -> array.elements(BTuple.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + BTuple.class.getCanonicalName() + "."));
  }

  @Test
  public void empty_array_is_empty() throws Exception {
    var array = exprDb().newArrayBuilder(arrayTB()).build();
    assertThat(array.elements(BString.class)).isEmpty();
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
    assertThat(array.elements(BString.class)).containsExactly(stringB("abc"));
  }

  @Test
  public void array_contains_added_elem_via_add_all_method() throws Exception {
    var string = stringB("abc");
    var string2 = stringB("def");
    var array =
        exprDb().newArrayBuilder(arrayTB()).addAll(list(string, string2)).build();
    assertThat(array.elements(BString.class)).containsExactly(string, string2).inOrder();
  }

  @Test
  public void array_contains_added_elements_in_order() throws Exception {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var string3 = stringB("ghi");
    var array = exprDb()
        .newArrayBuilder(arrayTB())
        .add(string1)
        .add(string2)
        .add(string3)
        .build();
    assertThat(array.elements(BString.class))
        .containsExactly(string1, string2, string3)
        .inOrder();
  }

  @Test
  public void adding_same_elem_twice_builds_array_with_two_elements() throws Exception {
    var string = stringB("abc");
    var array = exprDb().newArrayBuilder(arrayTB()).add(string).add(string).build();
    assertThat(array.elements(BString.class)).containsExactly(string, string);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BArray> {
    @Override
    protected List<BArray> equalExprs() throws BytecodeException {
      return list(arrayB(intB(0), intB(1)), arrayB(intB(0), intB(1)));
    }

    @Override
    protected List<BArray> nonEqualExprs() throws BytecodeException {
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
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash())).isEqualTo(array);
  }

  @Test
  public void array_read_by_hash_contains_same_elements() throws Exception {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(string1).add(string2).build();
    assertThat(((BArray) exprDbOther().get(array.hash())).elements(BString.class))
        .containsExactly(string1, string2)
        .inOrder();
  }

  @Test
  public void array_read_by_hash_has_same_hash() throws Exception {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash()).hash()).isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(BType elemT) throws Exception {
    var arrayTH = arrayTB(elemT);
    var arrayH = exprDb().newArrayBuilder(arrayTH).build();
    assertThat(arrayH.category()).isEqualTo(arrayTH);
  }

  private static List<BCategory> type_test_data() {
    return TestingCategoryB.CATS_TO_TEST;
  }

  @Test
  public void to_string() throws Exception {
    var string1 = stringB("abc");
    var string2 = stringB("def");
    var array = exprDb().newArrayBuilder(arrayTB()).add(string1).add(string2).build();
    assertThat(array.toString()).isEqualTo("""
            ["abc","def"]@""" + array.hash());
  }
}

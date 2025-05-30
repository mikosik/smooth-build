package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.testing.TestingBKind;

public class BArrayTest extends VmTestContext {
  @Test
  void empty_int_array_can_be_iterated_as_int() throws Exception {
    var array = newBArrayBuilder(bIntArrayType()).build();
    assertThat(array.elements(BInt.class)).isEmpty();
  }

  @Test
  void string_array_cannot_be_iterated_as_tuple() throws Exception {
    var array = newBArrayBuilder(bStringArrayType()).add(bString("abc")).build();
    assertCall(() -> array.elements(BTuple.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + BTuple.class.getCanonicalName() + "."));
  }

  @Test
  void empty_array_is_empty() throws Exception {
    var array = newBArrayBuilder(bArrayType()).build();
    assertThat(array.elements(BString.class)).isEmpty();
  }

  @Test
  void adding_null_is_forbidden() throws Exception {
    var arrayBuilder = newBArrayBuilder(bArrayType());
    assertCall(() -> arrayBuilder.add(null)).throwsException(NullPointerException.class);
  }

  @Test
  void adding_elem_with_wrong_type_is_forbidden() throws Exception {
    var arrayBuilder = newBArrayBuilder(bArrayType());
    assertCall(() -> arrayBuilder.add(bBlob(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  void array_contains_added_elem() throws Exception {
    var array = newBArrayBuilder(bArrayType()).add(bString("abc")).build();
    assertThat(array.elements(BString.class)).containsExactly(bString("abc"));
  }

  @Test
  void array_contains_added_elem_via_add_all_method() throws Exception {
    var string = bString("abc");
    var string2 = bString("def");
    var array = newBArrayBuilder(bArrayType()).addAll(list(string, string2)).build();
    assertThat(array.elements(BString.class)).containsExactly(string, string2).inOrder();
  }

  @Test
  void array_contains_added_elements_in_order() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var string3 = bString("ghi");
    var array =
        newBArrayBuilder(bArrayType()).add(string1).add(string2).add(string3).build();
    assertThat(array.elements(BString.class))
        .containsExactly(string1, string2, string3)
        .inOrder();
  }

  @Test
  void adding_same_elem_twice_builds_array_with_two_elements() throws Exception {
    var string = bString("abc");
    var array = newBArrayBuilder(bArrayType()).add(string).add(string).build();
    assertThat(array.elements(BString.class)).containsExactly(string, string);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BArray> {
    @Override
    protected List<BArray> equalExprs() throws BytecodeException {
      return list(bArray(bInt(0), bInt(1)), bArray(bInt(0), bInt(1)));
    }

    @Override
    protected List<BArray> nonEqualExprs() throws BytecodeException {
      return list(
          bArray(bIntType()),
          bArray(bStringType()),
          bArray(bInt(0)),
          bArray(bInt(1)),
          bArray(bInt(0), bInt(1)));
    }
  }

  @Test
  void array_can_be_read_by_hash() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = newBArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash())).isEqualTo(array);
  }

  @Test
  void array_read_by_hash_contains_same_elements() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = newBArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(((BArray) exprDbOther().get(array.hash())).elements(BString.class))
        .containsExactly(string1, string2)
        .inOrder();
  }

  @Test
  void array_read_by_hash_has_same_hash() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = newBArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash()).hash()).isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(BType elemType) throws Exception {
    var bArrayType = bArrayType(elemType);
    var bArray = newBArrayBuilder(bArrayType).build();
    assertThat(bArray.kind()).isEqualTo(bArrayType);
  }

  private static List<BKind> type_test_data() {
    return TestingBKind.TYPES_TO_TEST;
  }

  @Test
  void to_string() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = newBArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(array.toString())
        .isEqualTo(
            """
        BArray(
          hash = b948a36694c34568a77d7fdd23cadd7a7d8d5c62c26fe5585bbf19fabfa5647e
          type = [String]
          elements = [
            BString(
              hash = a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986
              type = String
              value = "abc"
            )
            BString(
              hash = 8481449ed8722fe7a7a6d1d3b1a731fca4385ce3e555e2e718aeb530ccfa4f2e
              type = String
              value = "def"
            )
          ]
        )""");
  }
}

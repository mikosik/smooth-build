package org.smoothbuild.virtualmachine.bytecode.expr.base;

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
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.testing.TestingBKind;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BArrayTest extends TestingVirtualMachine {
  @Test
  void empty_int_array_can_be_iterated_as_int() throws Exception {
    var array = exprDb().newArrayBuilder(bArrayType(bIntType())).build();
    assertThat(array.elements(BInt.class)).isEmpty();
  }

  @Test
  void string_array_cannot_be_iterated_as_tuple() throws Exception {
    var array =
        exprDb().newArrayBuilder(bArrayType(bStringType())).add(bString("abc")).build();
    assertCall(() -> array.elements(BTuple.class))
        .throwsException(new IllegalArgumentException(
            "[String] cannot be viewed as Iterable of " + BTuple.class.getCanonicalName() + "."));
  }

  @Test
  void empty_array_is_empty() throws Exception {
    var array = exprDb().newArrayBuilder(bArrayType()).build();
    assertThat(array.elements(BString.class)).isEmpty();
  }

  @Test
  void adding_null_is_forbidden() throws Exception {
    var arrayBuilder = exprDb().newArrayBuilder(bArrayType());
    assertCall(() -> arrayBuilder.add(null)).throwsException(NullPointerException.class);
  }

  @Test
  void adding_elem_with_wrong_type_is_forbidden() throws Exception {
    var arrayBuilder = exprDb().newArrayBuilder(bArrayType());
    assertCall(() -> arrayBuilder.add(bBlob(ByteString.of())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  void array_contains_added_elem() throws Exception {
    var array = exprDb().newArrayBuilder(bArrayType()).add(bString("abc")).build();
    assertThat(array.elements(BString.class)).containsExactly(bString("abc"));
  }

  @Test
  void array_contains_added_elem_via_add_all_method() throws Exception {
    var string = bString("abc");
    var string2 = bString("def");
    var array =
        exprDb().newArrayBuilder(bArrayType()).addAll(list(string, string2)).build();
    assertThat(array.elements(BString.class)).containsExactly(string, string2).inOrder();
  }

  @Test
  void array_contains_added_elements_in_order() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var string3 = bString("ghi");
    var array = exprDb()
        .newArrayBuilder(bArrayType())
        .add(string1)
        .add(string2)
        .add(string3)
        .build();
    assertThat(array.elements(BString.class))
        .containsExactly(string1, string2, string3)
        .inOrder();
  }

  @Test
  void adding_same_elem_twice_builds_array_with_two_elements() throws Exception {
    var string = bString("abc");
    var array = exprDb().newArrayBuilder(bArrayType()).add(string).add(string).build();
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
    var array = exprDb().newArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash())).isEqualTo(array);
  }

  @Test
  void array_read_by_hash_contains_same_elements() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = exprDb().newArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(((BArray) exprDbOther().get(array.hash())).elements(BString.class))
        .containsExactly(string1, string2)
        .inOrder();
  }

  @Test
  void array_read_by_hash_has_same_hash() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = exprDb().newArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(exprDbOther().get(array.hash()).hash()).isEqualTo(array.hash());
  }

  @ParameterizedTest
  @MethodSource("type_test_data")
  public void type(BType elemT) throws Exception {
    var arrayTH = bArrayType(elemT);
    var arrayH = exprDb().newArrayBuilder(arrayTH).build();
    assertThat(arrayH.kind()).isEqualTo(arrayTH);
  }

  private static List<BKind> type_test_data() {
    return TestingBKind.KINDS_TO_TEST;
  }

  @Test
  void to_string() throws Exception {
    var string1 = bString("abc");
    var string2 = bString("def");
    var array = exprDb().newArrayBuilder(bArrayType()).add(string1).add(string2).build();
    assertThat(array.toString()).isEqualTo("""
            ["abc","def"]@""" + array.hash());
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BConstructArrayTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bConstructArray = bConstructArray(bIntType());
    assertThat(bConstructArray.name()).isEqualTo("constructArray");
  }

  @Test
  void kind_returns_kind() throws Exception {
    var bConstructArray = bConstructArray(bIntType());
    assertThat(bConstructArray.kind()).isEqualTo(bConstructArrayKind(bIntType()));
  }

  @Test
  void creating_bConstructArray_with_elemT_different_than_required_causes_exception() {
    assertCall(() -> bConstructArray(bIntType(), bString("abc")).kind())
        .throwsException(new IllegalArgumentException(
            "`element0.evaluationType()` should be `Int` but is `String`."));
  }

  @Test
  void elemT_can_be_equal_elementT_specified_in_kind() throws Exception {
    bConstructArray(bIntArrayType(), bArray(bInt(3)));
  }

  @Test
  void elements_returns_elements() throws Exception {
    assertThat(bConstructArray(bInt(2)).elements()).isEqualTo(list(bInt(2)));
  }

  @Test
  void elements_is_cached() throws BytecodeException {
    var bConstructArray = bConstructArray(bInt(2));
    assertThat(bConstructArray.elements()).isSameInstanceAs(bConstructArray.elements());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BConstructArray> {
    @Override
    protected List<BConstructArray> equalExprs() throws BytecodeException {
      return list(bConstructArray(bInt(1), bInt(2)), bConstructArray(bInt(1), bInt(2)));
    }

    @Override
    protected List<BConstructArray> nonEqualExprs() throws BytecodeException {
      return list(
          bConstructArray(bIntType()),
          bConstructArray(bStringType()),
          bConstructArray(bInt(1)),
          bConstructArray(bInt(2)),
          bConstructArray(bInt(1), bInt(2)),
          bConstructArray(bInt(1), bInt(3)));
    }
  }

  @Test
  void array_can_be_read_back_by_hash() throws Exception {
    var bConstructArray = bConstructArray(bInt(1));
    assertThat(exprDbOther().get(bConstructArray.hash())).isEqualTo(bConstructArray);
  }

  @Test
  void array_read_back_by_hash_has_same_elementss() throws Exception {
    var bConstructArray = bConstructArray(bInt(1));
    assertThat(((BConstructArray) exprDbOther().get(bConstructArray.hash())).elements())
        .isEqualTo(list(bInt(1)));
  }

  @Test
  void to_string() throws Exception {
    var bConstructArray = bConstructArray(bInt(1));
    assertThat(bConstructArray.toString()).isEqualTo("""
        BConstructArray(
          hash = 32525892ab4d75f2b1f23293d34118c444fa06fe837ee9efaa2072032c879054
          evaluationType = [Int]
          elements = [
            BInt(
              hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
              type = Int
              value = 1
            )
          ]
        )""");
  }
}

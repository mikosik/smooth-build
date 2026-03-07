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

public class BArrayGetTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var array = bArray(bInt(7));
    var index = bInt(0);
    var bArrayGet = bArrayGet(array, index);
    assertThat(bArrayGet.name()).isEqualTo("arrayGet");
  }

  @Test
  void creating_arrayGet_with_non_array_expr_as_array_arg_causes_exception() {
    assertCall(() -> bArrayGet(bInt(3), bInt(2)))
        .throwsException(new IllegalArgumentException(
            "`array.evaluationType()` should be `BArrayType` but is `BIntType`."));
  }

  @Test
  void creating_arrayGet_with_non_int_expr_as_index_causes_exception() {
    assertCall(() -> bArrayGet(bArray(bBoolType()), bString()))
        .throwsException(new IllegalArgumentException(
            "`index.evaluationType()` should be `BIntType` but is `BStringType`."));
  }

  @Test
  void data_returns_array_and_index() throws Exception {
    var array = bArray(bInt(7));
    var index = bInt(0);
    var arrayGet = bArrayGet(array, index);
    assertThat(arrayGet.array()).isEqualTo(array);
    assertThat(arrayGet.index()).isEqualTo(index);
  }

  @Test
  void arrayGet_is_cached() throws BytecodeException {
    var arrayGet = bArrayGet(bArray(bInt(7)), bInt(0));
    assertThat(arrayGet.array()).isSameInstanceAs(arrayGet.array());
  }

  @Test
  void index_is_cached() throws BytecodeException {
    var arrayGet = bArrayGet(bArray(bInt(7)), bInt(0));
    assertThat(arrayGet.index()).isSameInstanceAs(arrayGet.index());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BArrayGet> {
    @Override
    protected List<BArrayGet> equalExprs() throws BytecodeException {
      return list(
          bArrayGet(bArray(bInt(7), bInt(9)), bInt(0)),
          bArrayGet(bArray(bInt(7), bInt(9)), bInt(0)));
    }

    @Override
    protected List<BArrayGet> nonEqualExprs() throws BytecodeException {
      return list(
          bArrayGet(bArray(bInt(1)), bInt(0)),
          bArrayGet(bArray(bInt(2)), bInt(0)),
          bArrayGet(bArray(bInt(2), bInt(2)), bInt(0)),
          bArrayGet(bArray(bInt(2), bInt(2)), bInt(1)),
          bArrayGet(bArray(bInt(2), bInt(7)), bInt(0)),
          bArrayGet(bArray(bInt(7), bInt(2)), bInt(0)));
    }
  }

  @Test
  void arrayGet_can_be_read_back_by_hash() throws Exception {
    var arrayGet = bArrayGet(bArray(bInt(7)), bInt(0));
    assertThat(exprDbOther().get(arrayGet.hash())).isEqualTo(arrayGet);
  }

  @Test
  void arrayGet_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = bArray(bInt(17), bInt(18));
    var index = bInt(0);
    var arrayGet = bArrayGet(array, index);
    var arrayGetRead = (BArrayGet) exprDbOther().get(arrayGet.hash());
    assertThat(arrayGetRead.array()).isEqualTo(array);
    assertThat(arrayGetRead.index()).isEqualTo(index);
  }

  @Test
  void to_string() throws Exception {
    var arrayGet = bArrayGet(bArray(bInt(17)), bInt(0));
    assertThat(arrayGet.toString()).isEqualTo("""
        BArrayGet(
          hash = 2a3ee0490047c831fed1b6bade199fc3c9f79d10b33e705a945a5eb171b0b385
          evaluationType = Int
          array = BArray(
            hash = 0bb233bbb42989f27846b6a121ff2570f12136aeababcf0d6fe1c57195bcc2a9
            type = [Int]
            elements = [
              BInt(
                hash = d6781a8034402f1bb1369df5042c4cc9d4d726044ba4ae8eb55efce43bad6ec5
                type = Int
                value = 17
              )
            ]
          )
          index = BInt(
            hash = 7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba
            type = Int
            value = 0
          )
        )""");
  }
}

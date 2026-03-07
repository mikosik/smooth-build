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

public class BTupleGetTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bTupleGet = bTupleGet(bTuple(bInt(7)), bInt(0));
    assertThat(bTupleGet.name()).isEqualTo("tupleGet");
  }

  @Test
  void creating_tupleGet_with_non_tuple_expr_causes_exception() {
    assertCall(() -> bTupleGet(bInt(3), bInt(2)))
        .throwsException(new IllegalArgumentException(
            "`tuple.evaluationType()` should be `BTupleType` but is `BIntType`."));
  }

  @Test
  void creating_tupleGet_with_too_great_index_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bTupleGet(tuple, bInt(2)).kind())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void creating_tupleGet_with_index_lower_than_zero_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bTupleGet(tuple, bInt(-1)).kind())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  void sub_expressions_contains_tuple_and_index() throws Exception {
    var tuple = bTuple(bInt(7));
    var index = bInt(0);
    var tupleGet = bTupleGet(tuple, index);
    assertThat(tupleGet.tuple()).isEqualTo(tuple);
    assertThat(tupleGet.index()).isEqualTo(index);
  }

  @Test
  void tuple_is_cached() throws BytecodeException {
    var tuple = bTuple(bString("abc"));
    var tupleGet = bTupleGet(tuple, bInt(0));
    assertThat(tupleGet.tuple()).isSameInstanceAs(tupleGet.tuple());
  }

  @Test
  void index_is_cached() throws BytecodeException {
    var tupleGet = bTupleGet(bTuple(bString("abc")), bInt(0));
    assertThat(tupleGet.index()).isSameInstanceAs(tupleGet.index());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BTupleGet> {
    @Override
    protected List<BTupleGet> equalExprs() throws BytecodeException {
      return list(
          bTupleGet(bTuple(bInt(7), bString("abc")), bInt(0)),
          bTupleGet(bTuple(bInt(7), bString("abc")), bInt(0)));
    }

    @Override
    protected List<BTupleGet> nonEqualExprs() throws BytecodeException {
      return list(
          bTupleGet(bTuple(bInt(1)), bInt(0)),
          bTupleGet(bTuple(bInt(2)), bInt(0)),
          bTupleGet(bTuple(bInt(2), bInt(2)), bInt(0)),
          bTupleGet(bTuple(bInt(2), bInt(2)), bInt(1)),
          bTupleGet(bTuple(bInt(2), bInt(7)), bInt(0)),
          bTupleGet(bTuple(bInt(7), bInt(2)), bInt(0)));
    }
  }

  @Test
  void tupleGet_can_be_read_back_by_hash() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    var tupleGet = bTupleGet(tuple, bInt(0));
    assertThat(exprDbOther().get(tupleGet.hash())).isEqualTo(tupleGet);
  }

  @Test
  void tupleGet_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var tuple = bAnimal();
    var index = bInt(0);
    var tupleGet = bTupleGet(tuple, index);
    var tupleGetRead = (BTupleGet) exprDbOther().get(tupleGet.hash());
    assertThat(tupleGetRead.tuple()).isEqualTo(tuple);
    assertThat(tupleGetRead.index()).isEqualTo(index);
  }

  @Test
  void to_string() throws Exception {
    var tupleGet = bTupleGet(bAnimal(), bInt(0));
    assertThat(tupleGet.toString()).isEqualTo("""
        BTupleGet(
          hash = b4c6333d5e5eddbaf6cc10f5f2ea298d7b7c163f71632dc7842c306f5f896d66
          evaluationType = String
          tuple = BTuple(
            hash = 2c46ab85d0281ae6d3389c32fc5e8a8d38865ae3b18f7dafe11676129d2c8f63
            type = {String,Int}
            elements = [
              BString(
                hash = b1f3b6cee6f8b6bb9fd67e58238157aa4267fb633b75c886e1b08b8e42c89175
                type = String
                value = "rabbit"
              )
              BInt(
                hash = b00b1c1fa3eb808c7898052142c9f222df725d8e5f3801b69326c4bc3c2d2809
                type = Int
                value = 7
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

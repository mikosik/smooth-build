package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BSelectTest extends VmTestContext {
  @Test
  void creating_select_with_non_tuple_expr_causes_exception() {
    assertCall(() -> bSelect(bInt(3), bInt(2)))
        .throwsException(new IllegalArgumentException(
            "`selectable.evaluationType()` should be `BTupleType` but is `BIntType`."));
  }

  @Test
  void creating_select_with_too_great_index_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bSelect(tuple, bInt(2)).kind())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void creating_select_with_index_lower_than_zero_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bSelect(tuple, bInt(-1)).kind())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  void sub_expressions_contains_tuple_and_index() throws Exception {
    var selectable = bTuple(bInt(7));
    var index = bInt(0);
    assertThat(bSelect(selectable, index).subExprs()).isEqualTo(new BSubExprs(selectable, index));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BSelect> {
    @Override
    protected List<BSelect> equalExprs() throws BytecodeException {
      return list(
          bSelect(bTuple(bInt(7), bString("abc")), bInt(0)),
          bSelect(bTuple(bInt(7), bString("abc")), bInt(0)));
    }

    @Override
    protected List<BSelect> nonEqualExprs() throws BytecodeException {
      return list(
          bSelect(bTuple(bInt(1)), bInt(0)),
          bSelect(bTuple(bInt(2)), bInt(0)),
          bSelect(bTuple(bInt(2), bInt(2)), bInt(0)),
          bSelect(bTuple(bInt(2), bInt(2)), bInt(1)),
          bSelect(bTuple(bInt(2), bInt(7)), bInt(0)),
          bSelect(bTuple(bInt(7), bInt(2)), bInt(0)));
    }
  }

  @Test
  void select_can_be_read_back_by_hash() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    var select = bSelect(tuple, bInt(0));
    assertThat(exprDbOther().get(select.hash())).isEqualTo(select);
  }

  @Test
  void select_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var selectable = bAnimal();
    var index = bInt(0);
    var select = bSelect(selectable, index);
    assertThat(((BSelect) exprDbOther().get(select.hash())).subExprs())
        .isEqualTo(new BSubExprs(selectable, index));
  }

  @Test
  void to_string() throws Exception {
    var select = bSelect(bAnimal(), bInt(0));
    assertThat(select.toString())
        .isEqualTo(
            """
        BSelect(
          hash = b4c6333d5e5eddbaf6cc10f5f2ea298d7b7c163f71632dc7842c306f5f896d66
          evaluationType = String
          selectable = BTuple(
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

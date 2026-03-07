package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BCreateTupleTest extends VmTestContext {
  @Test
  void name() throws BytecodeException {
    assertThat(bCreateTuple().name()).isEqualTo("createTuple");
  }

  @Test
  void kind_returns_kind() throws Exception {
    var bCreateTuple = bCreateTuple(bInt(3));
    assertThat(bCreateTuple.kind()).isEqualTo(bCreateTupleKind(bIntType()));
  }

  @Test
  void items_returns_items() throws Exception {
    assertThat(bCreateTuple(bInt(1), bString("abc")).items())
        .isEqualTo(list(bInt(1), bString("abc")));
  }

  @Test
  void items_is_cached() throws BytecodeException {
    var bCreateTuple = bCreateTuple(bInt(1), bString("abc"));
    assertThat(bCreateTuple.items()).isSameInstanceAs(bCreateTuple.items());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCreateTuple> {
    @Override
    protected List<BCreateTuple> equalExprs() throws BytecodeException {
      return list(bCreateTuple(bInt(1), bString("abc")), bCreateTuple(bInt(1), bString("abc")));
    }

    @Override
    protected List<BCreateTuple> nonEqualExprs() throws BytecodeException {
      return list(
          bCreateTuple(bInt(1)),
          bCreateTuple(bInt(2)),
          bCreateTuple(bString("abc")),
          bCreateTuple(bInt(1), bString("abc")));
    }
  }

  @Test
  void bCreateTuple_can_be_read_back_by_hash() throws Exception {
    var bCreateTuple = bCreateTuple(bInt(1));
    assertThat(exprDbOther().get(bCreateTuple.hash())).isEqualTo(bCreateTuple);
  }

  @Test
  void bCreateTuple_read_back_by_hash_has_same_items() throws Exception {
    var bCreateTuple = bCreateTuple(bInt(), bString());
    assertThat(((BCreateTuple) exprDbOther().get(bCreateTuple.hash())).items())
        .isEqualTo(list(bInt(), bString()));
  }

  @Test
  void to_string() throws Exception {
    var bCreateTuple = bCreateTuple(bInt(1));
    assertThat(bCreateTuple.toString()).isEqualTo("""
        BCreateTuple(
          hash = 06a264a951d27e6953fa12a624922cea7cbfd03ff7af071c9b7464990b20dc3b
          evaluationType = {Int}
          items = [
            BInt(
              hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
              type = Int
              value = 1
            )
          ]
        )""");
  }
}

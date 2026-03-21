package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BConstructTupleTest extends VmTestContext {
  @Test
  void name() throws BytecodeException {
    assertThat(bConstructTuple().name()).isEqualTo("constructTuple");
  }

  @Test
  void kind_returns_kind() throws Exception {
    var bConstructTuple = bConstructTuple(bInt(3));
    assertThat(bConstructTuple.kind()).isEqualTo(bConstructTupleKind(bIntType()));
  }

  @Test
  void items_returns_items() throws Exception {
    assertThat(bConstructTuple(bInt(1), bString("abc")).items())
        .isEqualTo(list(bInt(1), bString("abc")));
  }

  @Test
  void items_is_cached() throws BytecodeException {
    var bConstructTuple = bConstructTuple(bInt(1), bString("abc"));
    assertThat(bConstructTuple.items()).isSameInstanceAs(bConstructTuple.items());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BConstructTuple> {
    @Override
    protected List<BConstructTuple> equalExprs() throws BytecodeException {
      return list(
          bConstructTuple(bInt(1), bString("abc")), bConstructTuple(bInt(1), bString("abc")));
    }

    @Override
    protected List<BConstructTuple> nonEqualExprs() throws BytecodeException {
      return list(
          bConstructTuple(bInt(1)),
          bConstructTuple(bInt(2)),
          bConstructTuple(bString("abc")),
          bConstructTuple(bInt(1), bString("abc")));
    }
  }

  @Test
  void bConstructTuple_can_be_read_back_by_hash() throws Exception {
    var bConstructTuple = bConstructTuple(bInt(1));
    assertThat(exprDbOther().get(bConstructTuple.hash())).isEqualTo(bConstructTuple);
  }

  @Test
  void bConstructTuple_read_back_by_hash_has_same_items() throws Exception {
    var bConstructTuple = bConstructTuple(bInt(), bString());
    assertThat(((BConstructTuple) exprDbOther().get(bConstructTuple.hash())).items())
        .isEqualTo(list(bInt(), bString()));
  }

  @Test
  void to_string() throws Exception {
    var bConstructTuple = bConstructTuple(bInt(1));
    assertThat(bConstructTuple.toString()).isEqualTo("""
        BConstructTuple(
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

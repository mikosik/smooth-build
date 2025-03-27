package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BCombineTest extends VmTestContext {
  @Test
  void kind_returns_kind() throws Exception {
    var combine = bCombine(bInt(3));
    assertThat(combine.kind()).isEqualTo(bCombineKind(bIntType()));
  }

  @Test
  void items_returns_items() throws Exception {
    assertThat(bCombine(bInt(1), bString("abc")).items()).isEqualTo(list(bInt(1), bString("abc")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCombine> {
    @Override
    protected List<BCombine> equalExprs() throws BytecodeException {
      return list(bCombine(bInt(1), bString("abc")), bCombine(bInt(1), bString("abc")));
    }

    @Override
    protected List<BCombine> nonEqualExprs() throws BytecodeException {
      return list(
          bCombine(bInt(1)),
          bCombine(bInt(2)),
          bCombine(bString("abc")),
          bCombine(bInt(1), bString("abc")));
    }
  }

  @Test
  void combine_can_be_read_back_by_hash() throws Exception {
    var combine = bCombine(bInt(1));
    assertThat(exprDbOther().get(combine.hash())).isEqualTo(combine);
  }

  @Test
  void combine_read_back_by_hash_has_same_items() throws Exception {
    var combine = bCombine(bInt(), bString());
    assertThat(((BCombine) exprDbOther().get(combine.hash())).items())
        .isEqualTo(list(bInt(), bString()));
  }

  @Test
  void to_string() throws Exception {
    var combine = bCombine(bInt(1));
    assertThat(combine.toString())
        .isEqualTo(
            """
        BCombine(
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

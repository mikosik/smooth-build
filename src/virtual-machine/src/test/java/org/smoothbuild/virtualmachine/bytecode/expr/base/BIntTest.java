package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.math.BigInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BIntTest extends VmTestContext {
  @Test
  void type_of_int_is_int_type() throws Exception {
    assertThat(bInt(123).kind()).isEqualTo(bIntType());
  }

  @Test
  void to_j_returns_java_big_integer() throws Exception {
    assertThat(bInt(123).toJavaBigInteger()).isEqualTo(BigInteger.valueOf(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BInt> {
    @Override
    protected List<BInt> equalExprs() throws BytecodeException {
      return list(bInt(7), bInt(7));
    }

    @Override
    protected List<BInt> nonEqualExprs() throws BytecodeException {
      return list(bInt(-7), bInt(-1), bInt(0), bInt(1), bInt(7));
    }
  }

  @Test
  void int_can_be_read_back_by_hash() throws Exception {
    var i = bInt(123);
    assertThat(exprDbOther().get(i.hash())).isEqualTo(i);
  }

  @Test
  void int_read_back_by_hash_has_same_to_J() throws Exception {
    var i = bInt(123);
    assertThat(((BInt) exprDbOther().get(i.hash())).toJavaBigInteger())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  void to_string_contains_int_value() throws Exception {
    var i = bInt(123);
    assertThat(i.toString()).isEqualTo("123@" + i.hash());
  }
}

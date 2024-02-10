package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class IntBTest extends TestContext {
  @Test
  public void type_of_int_is_int_type() throws Exception {
    assertThat(intB(123).category()).isEqualTo(intTB());
  }

  @Test
  public void to_j_returns_java_big_integer() throws Exception {
    assertThat(intB(123).toJ()).isEqualTo(BigInteger.valueOf(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<IntB> {
    @Override
    protected List<IntB> equalExprs() throws BytecodeException {
      return list(intB(7), intB(7));
    }

    @Override
    protected List<IntB> nonEqualExprs() throws BytecodeException {
      return list(intB(-7), intB(-1), intB(0), intB(1), intB(7));
    }
  }

  @Test
  public void int_can_be_read_back_by_hash() throws Exception {
    IntB i = intB(123);
    assertThat(bytecodeDbOther().get(i.hash())).isEqualTo(i);
  }

  @Test
  public void int_read_back_by_hash_has_same_to_J() throws Exception {
    IntB i = intB(123);
    assertThat(((IntB) bytecodeDbOther().get(i.hash())).toJ()).isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string_contains_int_value() throws Exception {
    IntB i = intB(123);
    assertThat(i.toString()).isEqualTo("123@" + i.hash());
  }
}

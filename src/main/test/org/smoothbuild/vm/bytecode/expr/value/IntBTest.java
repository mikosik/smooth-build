package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.ExprBTestCase;

import com.google.common.truth.Truth;

public class IntBTest extends TestContext {
  @Test
  public void type_of_int_is_int_type() {
    assertThat(intB(123).category())
        .isEqualTo(intTB());
  }

  @Test
  public void to_j_returns_java_big_integer() {
    assertThat(intB(123).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<IntB> {
    @Override
    protected List<IntB> equalExprs() {
      return list(
          intB(7),
          intB(7)
      );
    }

    @Override
    protected List<IntB> nonEqualExprs() {
      return list(
          intB(-7),
          intB(-1),
          intB(0),
          intB(1),
          intB(7)
      );
    }
  }

  @Test
  public void int_can_be_read_back_by_hash() {
    IntB i = intB(123);
    Truth.assertThat(bytecodeDbOther().get(i.hash()))
        .isEqualTo(i);
  }

  @Test
  public void int_read_back_by_hash_has_same_to_J() {
    IntB i = intB(123);
    assertThat(((IntB) bytecodeDbOther().get(i.hash())).toJ())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string_contains_int_value() {
    IntB i = intB(123);
    assertThat(i.toString())
        .isEqualTo("123@" + i.hash());
  }
}

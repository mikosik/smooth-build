package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class VarBTest extends TestContext {
  @Test
  public void type_of_var_expr_is_var_type() throws Exception {
    assertThat(varB(intTB(), 123).category()).isEqualTo(varCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() throws Exception {
    assertThat(varB(123).index()).isEqualTo(intB(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<VarB> {
    @Override
    protected List<VarB> equalExprs() throws BytecodeException {
      return list(varB(intTB(), 1), varB(intTB(), 1));
    }

    @Override
    protected List<VarB> nonEqualExprs() throws BytecodeException {
      return list(varB(intTB(), 1), varB(intTB(), 2), varB(stringTB(), 1));
    }
  }

  @Test
  public void var_can_be_read_back_by_hash() throws Exception {
    var varB = varB(intTB(), 123);
    assertThat(bytecodeDbOther().get(varB.hash())).isEqualTo(varB);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() throws Exception {
    var varB = varB(intTB(), 123);
    assertThat(((VarB) bytecodeDbOther().get(varB.hash())).index()).isEqualTo(intB(123));
  }

  @Test
  public void to_string() throws Exception {
    var varB = varB(intTB(), 123);
    assertThat(varB.toString()).isEqualTo("VAR:Int(123)@" + varB.hash());
  }
}

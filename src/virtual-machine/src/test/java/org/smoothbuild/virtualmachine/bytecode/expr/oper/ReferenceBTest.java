package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ReferenceBTest extends TestingVirtualMachine {
  @Test
  public void type_of_var_expr_is_var_type() throws Exception {
    assertThat(referenceB(intTB(), 123).category()).isEqualTo(varCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() throws Exception {
    assertThat(referenceB(123).index()).isEqualTo(intB(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ReferenceB> {
    @Override
    protected List<ReferenceB> equalExprs() throws BytecodeException {
      return list(referenceB(intTB(), 1), referenceB(intTB(), 1));
    }

    @Override
    protected List<ReferenceB> nonEqualExprs() throws BytecodeException {
      return list(referenceB(intTB(), 1), referenceB(intTB(), 2), referenceB(stringTB(), 1));
    }
  }

  @Test
  public void reference_can_be_read_back_by_hash() throws Exception {
    var referenceB = referenceB(intTB(), 123);
    assertThat(exprDbOther().get(referenceB.hash())).isEqualTo(referenceB);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() throws Exception {
    var referenceB = referenceB(intTB(), 123);
    assertThat(((ReferenceB) exprDbOther().get(referenceB.hash())).index()).isEqualTo(intB(123));
  }

  @Test
  public void to_string() throws Exception {
    var referenceB = referenceB(intTB(), 123);
    assertThat(referenceB.toString()).isEqualTo("VAR:Int(123)@" + referenceB.hash());
  }
}

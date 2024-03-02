package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class IfFuncBTest extends TestingVirtualMachine {
  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<IfFuncB> {
    @Override
    protected List<IfFuncB> equalExprs() throws BytecodeException {
      return list(ifFuncB(intTB()), ifFuncB(intTB()));
    }

    @Override
    protected List<IfFuncB> nonEqualExprs() throws BytecodeException {
      return list(ifFuncB(intTB()), ifFuncB(stringTB()), ifFuncB(blobTB()));
    }
  }

  @Test
  public void if_can_be_read_back_by_hash() throws Exception {
    var ifB = ifFuncB(intTB());
    assertThat(exprDbOther().get(ifB.hash())).isEqualTo(ifB);
  }

  @Test
  public void to_string() throws Exception {
    var ifB = ifFuncB(intTB());
    assertThat(ifB.toString()).isEqualTo("IfFunc((Bool,Int,Int)->Int)@" + ifB.hash());
  }
}

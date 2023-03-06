package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class ClosurizeBTest extends TestContext {
  @Test
  public void category_return_category() {
    var funcB = exprFuncB(list(intTB()), stringB("abc"));
    var closurizeB = closurizeB(funcB);
    assertThat(closurizeB.category())
        .isEqualTo(closurizeCB(funcB.type()));
  }

  @Test
  public void func_returns_func() {
    var funcB = exprFuncB(list(intTB()), stringB("abc"));
    var closurizeB = closurizeB(funcB);
    assertThat(closurizeB.func())
        .isEqualTo(funcB);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ClosurizeB> {
    @Override
    protected List<ClosurizeB> equalExprs() {
      return list(
          closurizeB(list(intTB()), stringB("abc")),
          closurizeB(list(intTB()), stringB("abc"))
      );
    }

    @Override
    protected List<ClosurizeB> nonEqualExprs() {
      return list(
          closurizeB(list(intTB()), stringB("abc")),
          closurizeB(list(intTB()), stringB("def")),
          closurizeB(list(blobTB()), stringB("abc")),
          closurizeB(list(blobTB()), stringB("def"))
      );
    }
  }

  @Test
  public void closurize_can_be_read_back_by_hash() {
    var funcB = exprFuncB(list(intTB()), stringB("abc"));
    var closurizeB = closurizeB(funcB);
    assertThat(bytecodeDbOther().get(closurizeB.hash()))
        .isEqualTo(closurizeB);
  }

  @Test
  public void to_string() {
    var funcB = exprFuncB(list(intTB()), stringB("abc"));
    var closurizeB = closurizeB(funcB);
    assertThat(closurizeB.toString())
        .isEqualTo("CLOSURIZE:(Int)->String(???)@" + closurizeB.hash());
  }
}

package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BLambdaRefTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bLambdaRef = bLambdaRef(bLambdaType(), bInt());
    assertThat(bLambdaRef.name()).isEqualTo("lambdaRef");
  }

  @Test
  void kind() throws Exception {
    assertThat(bLambdaRef(bLambdaType(), bInt()).kind()).isEqualTo(bLambdaRefKind(bLambdaType()));
  }

  @Test
  void lambda_name_returns_stored_value() throws Exception {
    assertThat(bLambdaRef(bLambdaType(), bInt(7)).lambdaName()).isEqualTo(bInt(7));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BLambdaRef> {
    @Override
    protected List<BLambdaRef> equalExprs() throws BytecodeException {
      return list(bLambdaRef(bLambdaType(), bInt(7)), bLambdaRef(bLambdaType(), bInt(7)));
    }

    @Override
    protected List<BLambdaRef> nonEqualExprs() throws BytecodeException {
      return List.list(
          bLambdaRef(bLambdaType(bIntType()), bInt(1)),
          bLambdaRef(bLambdaType(bIntType()), bInt(2)),
          bLambdaRef(bLambdaType(bStringType()), bInt(1)));
    }
  }

  @Test
  void lambda_ref_can_be_read_back_by_hash() throws Exception {
    var reference = bLambdaRef(bLambdaType(bIntType()), bInt(1));
    assertThat(exprDbOther().get(reference.hash())).isEqualTo(reference);
  }

  @Test
  void const_read_back_by_hash_has_same_value() throws Exception {
    var reference = bLambdaRef(bLambdaType(bIntType()), bInt(7));
    assertThat(((BLambdaRef) exprDbOther().get(reference.hash())).lambdaName()).isEqualTo(bInt(7));
  }

  @Test
  void to_string() throws Exception {
    var reference = bLambdaRef(bLambdaType(bIntType()), bInt(1));
    assertThat(reference.toString())
        .isEqualTo(
            """
                BLambdaRef(
                  hash = 7a6256a4193cb69b3b3016f0fe5ccf5723d557ee2f9a18e1cf00a818683f5b6c
                  evaluationType = ()->Int
                  lambdaName = BInt(
                    hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
                    type = Int
                    value = 1
                  )
                )""");
  }
}

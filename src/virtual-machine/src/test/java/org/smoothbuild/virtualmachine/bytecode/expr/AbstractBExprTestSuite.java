package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public abstract class AbstractBExprTestSuite<T extends BExpr> extends VmTestContext {
  protected abstract List<T> equalExprs() throws Exception;

  protected abstract List<T> nonEqualExprs() throws Exception;

  @Test
  void test_equals_and_hashcode_of_equal_exprs() throws Exception {
    new EqualsTester().addEqualityGroup((Object[]) equalExprs().toArray()).testEquals();
  }

  @Test
  void test_equals_and_hashcode_of_inequal_exprs() throws Exception {
    var equalsTester = new EqualsTester();
    for (T value : nonEqualExprs()) {
      equalsTester.addEqualityGroup(value);
    }
    equalsTester.testEquals();
  }

  @Test
  void test_hash_of_equal_exprs() throws Exception {
    var values = equalExprs();
    assertThat(values.get(0).hash()).isEqualTo(values.get(1).hash());
  }

  @Test
  void test_hash_of_inequal_exprs() throws Exception {
    var values = nonEqualExprs();
    for (int i = 0; i < values.size(); i++) {
      for (int j = i + 1; j < values.size(); j++) {
        T valueI = values.get(i);
        T valueJ = values.get(j);
        assertWithMessage("Comparing hashes of:\n" + i + ": " + valueI + "\n" + j + ": " + valueJ)
            .that(valueI.hash())
            .isNotEqualTo(valueJ.hash());
      }
    }
  }
}

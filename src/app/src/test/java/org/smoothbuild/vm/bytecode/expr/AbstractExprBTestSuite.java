package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

import com.google.common.testing.EqualsTester;

public abstract class AbstractExprBTestSuite<T extends ExprB> extends TestContext {
  protected abstract List<T> equalExprs();

  protected abstract List<T> nonEqualExprs();

  @Test
  public void test_equals_and_hashcode_of_equal_exprs() {
    new EqualsTester()
        .addEqualityGroup(equalExprs().toArray())
        .testEquals();
  }

  @Test
  public void test_equals_and_hashcode_of_inequal_exprs() {
    var equalsTester = new EqualsTester();
    for (T value : nonEqualExprs()) {
      equalsTester.addEqualityGroup(value);
    }
    equalsTester.testEquals();
  }

  @Test
  public void test_hash_of_equal_exprs() {
    var values = equalExprs();
    assertThat(values.get(0).hash())
        .isEqualTo(values.get(1).hash());
  }

  @Test
  public void test_hash_of_inequal_exprs() {
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

package org.smoothbuild.bytecode.obj;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.testing.TestContext;

import com.google.common.testing.EqualsTester;

public abstract class ObjBTestCase<T extends ObjB> extends TestContext {
  protected abstract List<T> equalValues();

  protected abstract List<T> nonEqualValues();

  @Test
  public void test_equals_and_hashcode_of_equal_objs() {
    new EqualsTester()
        .addEqualityGroup(equalValues().toArray())
        .testEquals();
  }

  @Test
  public void test_equals_and_hashcode_of_inequal_objs() {
    var equalsTester = new EqualsTester();
    for (T value : nonEqualValues()) {
      equalsTester.addEqualityGroup(value);
    }
    equalsTester.testEquals();
  }

  @Test
  public void test_hash_of_equal_objs() {
    var values = equalValues();
    assertThat(values.get(0).hash())
        .isEqualTo(values.get(1).hash());
  }

  @Test
  public void test_hash_of_inequal_objs() {
    var values = nonEqualValues();
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

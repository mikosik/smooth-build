package org.smoothbuild.db.record.spec;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractRecordSpecTestCase extends TestingContext {
  protected abstract Spec getSpec(RecordDb recordDb);

  @Test
  public void spec_is_cached() {
    assertThat(getSpec(recordDb()))
        .isSameInstanceAs(getSpec(recordDb()));
  }

  public static void assertSpecsAreDeeplyEqual(Spec actual, Spec expected) {
    assertThat(expected.name().equals(actual.name())
        && expected.hash().equals(actual.hash())
        && expected.isNothing() == actual.isNothing()
        && expected.kind().equals(actual.kind())
        && expected.jType().equals(actual.jType())
        && expected.toString().equals(expected.toString()))
        .isTrue();
  }
}

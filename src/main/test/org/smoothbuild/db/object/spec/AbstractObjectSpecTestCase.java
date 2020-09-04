package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractObjectSpecTestCase extends TestingContext {
  protected abstract Spec getSpec(ObjectDb objectDb);

  @Test
  public void spec_is_cached() {
    assertThat(getSpec(objectDb()))
        .isSameInstanceAs(getSpec(objectDb()));
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

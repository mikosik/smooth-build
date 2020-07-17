package org.smoothbuild.record.spec;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.db.RecordDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractRecordSpecTestCase extends TestingContext {
  protected abstract Spec getSpec(RecordDb recordDb);

  @Test
  public void spec_can_be_read_back() {
    Spec spec = getSpec(recordDb());
    assertSpecsAreDeeplyEqual((Spec) recordDbOther().get(spec.hash()), spec);
  }

  @Test
  public void spec_is_cached() {
    assertThat(getSpec(recordDb()))
        .isSameInstanceAs(getSpec(recordDb()));
  }

  @Test
  public void spec_is_cached_when_read_by_hash() {
    Spec spec = getSpec(recordDb());
    assertThat(recordDb().get(spec.hash()))
        .isSameInstanceAs(spec);
  }

  @Test
  public void spec_is_cached_when_read_twice_by_hash() {
    Hash hash = getSpec(recordDb()).hash();
    RecordDb recordDbOther = recordDbOther();
    Spec spec = (Spec) recordDbOther.get(hash);
    assertThat(recordDbOther.get(hash))
        .isSameInstanceAs(spec);
  }

  public static void assertSpecsAreDeeplyEqual(Spec actual, Spec expected) {
    assertThat(expected.name().equals(actual.name())
        && expected.hash().equals(actual.hash())
        && expected.dataHash().equals(actual.dataHash())
        && expected.isNothing() == actual.isNothing()
        && expected.kind().equals(actual.kind())
        && expected.jType().equals(actual.jType())
        && expected.spec().equals(expected.spec())
        && expected.toString().equals(expected.toString()))
        .isTrue();
  }
}

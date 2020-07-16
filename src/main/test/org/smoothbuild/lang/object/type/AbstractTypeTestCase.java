package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractTypeTestCase extends TestingContext {
  protected abstract BinaryType getType(ObjectDb objectDb);

  @Test
  public void type_can_be_read_back() {
    BinaryType type = getType(objectDb());
    assertTypesAreDeeplyEqual((BinaryType) objectDbOther().get(type.hash()), type);
  }

  @Test
  public void type_is_cached() {
    assertThat(getType(objectDb()))
        .isSameInstanceAs(getType(objectDb()));
  }

  @Test
  public void type_is_cached_when_read_by_hash() {
    BinaryType type = getType(objectDb());
    assertThat(objectDb().get(type.hash()))
        .isSameInstanceAs(type);
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() {
    Hash hash = getType(objectDb()).hash();
    ObjectDb objectDbOther = objectDbOther();
    BinaryType type = (BinaryType) objectDbOther.get(hash);
    assertThat(objectDbOther.get(hash))
        .isSameInstanceAs(type);
  }

  public static void assertTypesAreDeeplyEqual(BinaryType actual, BinaryType expected) {
    assertThat(expected.name().equals(actual.name())
        && expected.hash().equals(actual.hash())
        && expected.dataHash().equals(actual.dataHash())
        && expected.isNothing() == actual.isNothing()
        && expected.kind().equals(actual.kind())
        && expected.jType().equals(actual.jType())
        && expected.type().equals(expected.type())
        && expected.toString().equals(expected.toString()))
        .isTrue();
  }
}

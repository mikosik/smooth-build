package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractTypeTestCase extends TestingContext {
  protected ConcreteType type;
  protected Hash hash;
  private ValuesDb valuesDbOther;

  protected abstract ConcreteType getType(ValuesDb valuesDb);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(valuesDb()));
    when(() -> valuesDbOther().get(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(valuesDb()));
    when(() -> getType(valuesDb()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(valuesDb()));
    when(() -> valuesDb().get(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(valuesDb()).hash());
    given(valuesDbOther = valuesDbOther());
    given(type = (ConcreteType) valuesDbOther.get(hash));
    when(() -> valuesDbOther.get(hash));
    thenReturned(same(type));
  }
}

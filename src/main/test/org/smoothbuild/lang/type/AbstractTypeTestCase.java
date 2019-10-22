package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;

import com.google.common.hash.HashCode;

public abstract class AbstractTypeTestCase {
  protected HashedDb hashedDb;
  protected ValuesDb valuesDb;
  protected ConcreteType type;
  protected HashCode hash;

  @Before
  public void before() {
    given(hashedDb = new TestingHashedDb());
    given(valuesDb = new ValuesDb(hashedDb));
  }

  protected abstract ConcreteType getType(ValuesDb valuesDb);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(valuesDb));
    when(() -> newValuesDb().get(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(valuesDb));
    when(() -> getType(valuesDb));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(valuesDb));
    when(() -> valuesDb.get(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(valuesDb).hash());
    given(valuesDb = newValuesDb());
    given(type = (ConcreteType) valuesDb.get(hash));
    when(() -> valuesDb.get(hash));
    thenReturned(same(type));
  }

  private ValuesDb newValuesDb() {
    return new ValuesDb(hashedDb);
  }
}

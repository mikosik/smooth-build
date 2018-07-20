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

import com.google.common.hash.HashCode;

public abstract class AbstractTypeTestCase {
  protected HashedDb hashedDb;
  protected TypesDb typesDb;
  protected ConcreteType type;
  protected HashCode hash;

  @Before
  public void before() {
    given(hashedDb = new TestingHashedDb());
    given(typesDb = new TypesDb(hashedDb));
  }

  protected abstract ConcreteType getType(TypesDb typesDb);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(typesDb));
    when(() -> newTypesDb().read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(typesDb));
    when(() -> getType(typesDb));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(typesDb));
    when(() -> typesDb.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(typesDb).hash());
    given(typesDb = newTypesDb());
    given(type = typesDb.read(hash));
    when(() -> typesDb.read(hash));
    thenReturned(same(type));
  }

  private TypesDb newTypesDb() {
    return new TypesDb(hashedDb);
  }
}

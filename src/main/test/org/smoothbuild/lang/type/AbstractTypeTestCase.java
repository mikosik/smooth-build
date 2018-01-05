package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public abstract class AbstractTypeTestCase {
  protected HashedDb hashedDb;
  protected TypeSystem typeSystem;
  protected Type type;
  protected HashCode hash;

  @Before
  public void before() {
    given(hashedDb = new HashedDb());
    given(typeSystem = new TypeSystem(new TypesDb(hashedDb)));
  }

  protected abstract Type getType(TypeSystem typeSystem);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(typeSystem));
    when(() -> newTypeSystem().read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(typeSystem));
    when(() -> getType(typeSystem));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(typeSystem));
    when(() -> typeSystem.read(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(typeSystem).hash());
    given(typeSystem = newTypeSystem());
    given(type = typeSystem.read(hash));
    when(() -> typeSystem.read(hash));
    thenReturned(same(type));
  }

  private TypeSystem newTypeSystem() {
    return new TypeSystem(new TypesDb(hashedDb));
  }
}

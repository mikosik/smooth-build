package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractTypeTestCase extends TestingContext {
  protected ConcreteType type;
  protected Hash hash;
  private ObjectsDb objectsDbOther;

  protected abstract ConcreteType getType(ObjectsDb objectsDb);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(objectsDb()));
    when(() -> objectsDbOther().get(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(objectsDb()));
    when(() -> getType(objectsDb()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(objectsDb()));
    when(() -> objectsDb().get(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(objectsDb()).hash());
    given(objectsDbOther = objectsDbOther());
    given(type = (ConcreteType) objectsDbOther.get(hash));
    when(() -> objectsDbOther.get(hash));
    thenReturned(same(type));
  }
}

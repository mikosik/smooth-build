package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public abstract class AbstractTypeTestCase extends TestingContext {
  protected ConcreteType type;
  protected Hash hash;
  private ObjectDb objectDbOther;

  protected abstract ConcreteType getType(ObjectDb objectDb);

  @Test
  public void type_can_be_read_back() throws Exception {
    given(type = getType(objectDb()));
    when(() -> objectDbOther().get(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void type_is_cached() throws Exception {
    given(type = getType(objectDb()));
    when(() -> getType(objectDb()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_by_hash() throws Exception {
    given(type = getType(objectDb()));
    when(() -> objectDb().get(type.hash()));
    thenReturned(same(type));
  }

  @Test
  public void type_is_cached_when_read_twice_by_hash() throws Exception {
    given(hash = getType(objectDb()).hash());
    given(objectDbOther = objectDbOther());
    given(type = (ConcreteType) objectDbOther.get(hash));
    when(() -> objectDbOther.get(hash));
    thenReturned(same(type));
  }
}

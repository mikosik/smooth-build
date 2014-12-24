package org.smoothbuild.lang.type;

import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class NothingTypeTest {
  private Type type;
  private FakeObjectsDb objectsDb;

  @Test
  public void nothing_type_does_not_have_default_value() {
    given(type = Types.NOTHING);
    given(objectsDb = new FakeObjectsDb());
    when(type).defaultValue(objectsDb);
    thenThrown(UnsupportedOperationException.class);
  }
}

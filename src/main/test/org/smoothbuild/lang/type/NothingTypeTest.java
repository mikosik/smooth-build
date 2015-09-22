package org.smoothbuild.lang.type;

import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;

public class NothingTypeTest {
  private Type type;
  private ObjectsDb objectsDb;

  @Test
  public void nothing_type_does_not_have_default_value() {
    given(type = Types.NOTHING);
    given(objectsDb = objectsDb());
    when(type).defaultValue(objectsDb);
    thenThrown(UnsupportedOperationException.class);
  }
}

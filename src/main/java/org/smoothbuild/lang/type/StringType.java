package org.smoothbuild.lang.type;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

public class StringType extends Type {
  protected StringType() {
    super("String", SString.class);
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    return objectsDb.string("");
  }

  @Override
  public boolean isAllowedAsResult() {
    return true;
  }
}

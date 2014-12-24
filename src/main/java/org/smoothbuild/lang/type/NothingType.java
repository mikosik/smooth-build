package org.smoothbuild.lang.type;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.Value;

public class NothingType extends Type {
  protected NothingType() {
    super("Nothing", Nothing.class);
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    throw new UnsupportedOperationException("Nothing type doesn't have default value.");
  }
}

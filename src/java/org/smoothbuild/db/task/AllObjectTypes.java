package org.smoothbuild.db.task;

import org.smoothbuild.db.hash.EnumValues;

public class AllObjectTypes extends EnumValues<ObjectType> {
  public static final AllObjectTypes INSTANCE = new AllObjectTypes();

  private AllObjectTypes() {
    super(ObjectType.values());
  }
}

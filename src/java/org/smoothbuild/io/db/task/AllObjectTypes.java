package org.smoothbuild.io.db.task;

import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.io.db.hash.EnumValues;

public class AllObjectTypes extends EnumValues<Type> {
  public static final AllObjectTypes INSTANCE = new AllObjectTypes();

  private AllObjectTypes() {
    super(STRING, STRING_SET, FILE, FILE_SET);
  }
}

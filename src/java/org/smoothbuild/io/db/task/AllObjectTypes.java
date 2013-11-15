package org.smoothbuild.io.db.task;

import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;

import org.smoothbuild.io.db.hash.EnumValues;
import org.smoothbuild.lang.function.base.Type;

public class AllObjectTypes extends EnumValues<Type> {
  public static final AllObjectTypes INSTANCE = new AllObjectTypes();

  private AllObjectTypes() {
    super(STRING, STRING_SET, FILE, FILE_SET);
  }
}

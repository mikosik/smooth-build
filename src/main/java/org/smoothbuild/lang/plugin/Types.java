package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

public interface Types {
  public Type string();

  public Type blob();

  public Type generic();

  public StructType file();

  public Type getType(String name);

  public ArrayType array(Type elementType);
}

package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.ConcreteType;

public interface Types {
  public ConcreteType string();

  public ConcreteType blob();

  public ConcreteType nothing();

  public StructType file();

  public ConcreteType getType(String name);

  public ArrayType array(ConcreteType elementType);
}

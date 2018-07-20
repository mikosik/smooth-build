package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;

public interface Types {
  public ConcreteType string();

  public ConcreteType blob();

  public ConcreteType nothing();

  public StructType file();

  public ConcreteType getType(String name);

  public ConcreteArrayType array(ConcreteType elementType);
}

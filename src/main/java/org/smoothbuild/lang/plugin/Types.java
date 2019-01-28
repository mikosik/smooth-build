package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

public interface Types {
  public ConcreteType bool();

  public ConcreteType string();

  public ConcreteType blob();

  public ConcreteType nothing();

  public StructType file();

  public StructType message();

  public Type getType(String name);

  public ArrayType array(Type elementType);
}

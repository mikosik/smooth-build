package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.NamedList;

/**
 * This class is immutable.
 */
public class StructTypeImpl extends SType implements StructType {
  private final NamedList<Type> fields;

  public StructTypeImpl(String name, NamedList<Type> fields) {
    super(name, calculateVariables(fields));
    this.fields = fields;
  }


  @Override
  public NamedList<Type> fields() {
    return fields;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTypeImpl thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }
}

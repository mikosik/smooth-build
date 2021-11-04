package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.util.collect.NamedList;

/**
 * This class is immutable.
 */
public class StructTypeS extends TypeS implements StructType {
  private final NamedList<TypeS> fields;

  public StructTypeS(String name, NamedList<TypeS> fields) {
    super(name, calculateVariables(fields));
    this.fields = fields;
  }


  @Override
  public NamedList<TypeS> fields() {
    return fields;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTypeS thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }
}

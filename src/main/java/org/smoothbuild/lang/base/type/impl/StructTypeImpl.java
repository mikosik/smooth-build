package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class StructTypeImpl extends AbstractTypeImpl implements StructType {
  private final NamedList<Type> fields;

  public StructTypeImpl(String name, ImmutableList<? extends Named<? extends Type>> fields) {
    super(name, calculateVariables(fields));
    @SuppressWarnings("unchecked") // safe because both ImmutableList and Named are immutable
    var castFields = (ImmutableList<Named<Type>>) fields;
    this.fields = namedList(castFields);
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

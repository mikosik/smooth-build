package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.StructType.calculateVariables;
import static org.smoothbuild.lang.base.type.api.StructTypes.fieldsMap;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructTypeImpl extends AbstractTypeImpl implements StructType {
  private final ImmutableList<? extends Type> fields;
  private final ImmutableList<String> names;
  private final ImmutableMap<String, Integer> nameToIndex;

  public StructTypeImpl(String name, ImmutableList<? extends Type> fields,
      ImmutableList<String> names) {
    super(name, calculateVariables(fields));
    checkArgument(fields.size() == names.size(), "fields and names must have equal sizes");
    this.fields = fields;
    this.names = names;
    this.nameToIndex = fieldsMap(names);
  }

  @Override
  public ImmutableList<? extends Type> fields() {
    return fields;
  }

  @Override
  public ImmutableList<String> names() {
    return names;
  }

  @Override
  public ImmutableMap<String, Integer> nameToIndex() {
    return nameToIndex;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTypeImpl thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields)
        && this.names.equals(thatStruct.names);
  }
}

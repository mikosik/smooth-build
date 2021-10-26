package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.calculateVariables;
import static org.smoothbuild.lang.base.type.help.StructTypeImplHelper.fieldsMap;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructTypeImpl extends AbstractTypeImpl implements StructType {
  private final ImmutableList<? extends Type> fields;
  private final ImmutableList<Optional<String>> names;
  private final ImmutableMap<String, Integer> nameToIndex;

  public StructTypeImpl(String name, ImmutableList<? extends Named<? extends Type>> fields) {
    super(name, calculateVariables(fields));
    this.fields = map(fields, Named::object);
    this.names = map(fields, Named::name);
    this.nameToIndex = fieldsMap(names);
  }

  @Override
  public ImmutableList<? extends Type> fields() {
    return fields;
  }

  @Override
  public ImmutableList<Optional<String>> names() {
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

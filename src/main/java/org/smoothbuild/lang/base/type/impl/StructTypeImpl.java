package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Sets.set;

import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class is immutable.
 */
public class StructTypeImpl extends AbstractType implements StructType {
  private final ImmutableList<Type> fields;
  private final ImmutableMap<String, Integer> nameToIndex;

  public StructTypeImpl(String name, ImmutableList<Type> fields, ImmutableList<String> names) {
    super(name, set());
    checkArgument(fields.size() == names.size(), "fields and names must have equal sizes");
    this.fields = fields;
    this.nameToIndex = fieldsMap(names);
  }

  private static ImmutableMap<String, Integer> fieldsMap(ImmutableList<String> names) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < names.size(); i++) {
      builder.put(names.get(i), i);
    }
    return builder.build();
  }

  @Override
  public ImmutableList<Type> fields() {
    return fields;
  }

  public ImmutableMap<String, Integer> nameToIndex() {
    return nameToIndex;
  }

  @Override
  public boolean containsFieldWithName(String name) {
    return nameToIndex.containsKey(name);
  }

  @Override
  public Type fieldWithName(String name) {
    return fields.get(fieldIndex(name));
  }

  @Override
  public int fieldIndex(String name) {
    return nameToIndex.get(name);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTypeImpl thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields)
        && this.nameToIndex.equals(thatStruct.nameToIndex);
  }
}

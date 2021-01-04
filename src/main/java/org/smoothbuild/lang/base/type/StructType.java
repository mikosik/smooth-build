package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.List;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructType extends Type {
  private final ImmutableList<ItemSignature> fields;
  private final ImmutableMap<String, Integer> fieldNameToIndex;

  public StructType(String name, ImmutableList<ItemSignature> fields) {
    super(name, new TypeConstructor(name), false);
    this.fields = fields;
    this.fieldNameToIndex = fieldsMap(fields);
  }

  private static ImmutableMap<String, Integer> fieldsMap(List<ItemSignature> fields) {
    return IntStream.range(0, fields.size())
        .boxed()
        .collect(toImmutableMap(i -> fields.get(i).name(), i -> i));
  }

  public ImmutableList<ItemSignature> fields() {
    return fields;
  }

  public boolean containsFieldWithName(String name) {
    return fieldNameToIndex.containsKey(name);
  }

  public ItemSignature fieldWithName(String name) {
    return fields.get(fieldIndex(name));
  }

  public int fieldIndex(String name) {
    return fieldNameToIndex.get(name);
  }

  @Override
  public <T> T visit(TypeVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof StructType thatStruct) {
      return this.name().equals(thatStruct.name())
          && this.fields.equals(thatStruct.fields);
    }
    return false;
  }
}

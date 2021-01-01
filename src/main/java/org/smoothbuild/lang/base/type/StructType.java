package org.smoothbuild.lang.base.type;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.smoothbuild.lang.base.Item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class StructType extends Type {
  private final ImmutableList<Item> fields;
  private final ImmutableMap<String, Integer> fieldNameToIndex;

  public StructType(String name, ImmutableList<Item> fields) {
    super(name, false);
    this.fields = fields;
    this.fieldNameToIndex = fieldsMap(fields);
  }

  private static ImmutableMap<String, Integer> fieldsMap(List<? extends Item> fields) {
    return IntStream.range(0, fields.size())
        .boxed()
        .collect(toImmutableMap(i -> fields.get(i).name(), i -> i));
  }

  public ImmutableList<Item> fields() {
    return fields;
  }

  public boolean containsFieldWithName(String name) {
    return fieldNameToIndex.containsKey(name);
  }

  public Item fieldWithName(String name) {
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

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}

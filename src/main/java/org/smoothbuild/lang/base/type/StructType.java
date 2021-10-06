package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Sets.set;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class is immutable.
 */
public class StructType extends Type {
  private final ImmutableList<ItemSignature> fields;
  private final ImmutableMap<String, Integer> fieldNameToIndex;

  public StructType(String name, ImmutableList<ItemSignature> fields) {
    super(name, set());
    this.fields = fields;
    this.fieldNameToIndex = fieldsMap(fields);
  }

  private static ImmutableMap<String, Integer> fieldsMap(List<ItemSignature> fields) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < fields.size(); i++) {
      Optional<String> name = fields.get(i).name();
      if (name.isPresent()) {
        builder.put(name.get(), i);
      }
    }
    return builder.build();
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
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructType thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }
}

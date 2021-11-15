package org.smoothbuild.lang.base.type.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public class StructTypeS extends TypeS {
  private final NList<ItemSignature> fields;

  public StructTypeS(String name, NList<ItemSignature> fields) {
    super(name, calculateVariables(fields));
    this.fields = fields;
  }

  private static ImmutableSet<VariableS> calculateVariables(NList<ItemSignature> fields) {
    return fields.stream()
        .map(f -> f.type().variables())
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  public NList<ItemSignature> fields() {
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

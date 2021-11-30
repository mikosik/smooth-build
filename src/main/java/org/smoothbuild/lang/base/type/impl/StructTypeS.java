package org.smoothbuild.lang.base.type.impl;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;

import java.util.Collection;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class StructTypeS extends TypeS {
  private final NList<ItemSigS> fields;

  public StructTypeS(String name, NList<ItemSigS> fields) {
    super(name, calculateVars(fields));
    this.fields = fields;
  }

  private static ImmutableSet<VarS> calculateVars(NList<ItemSigS> fields) {
    return fields.stream()
        .map(f -> f.type().vars())
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  public NList<ItemSigS> fields() {
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

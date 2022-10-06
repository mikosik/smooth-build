package org.smoothbuild.compile.lang.type;

import static org.smoothbuild.compile.lang.type.TypeNamesS.tupleTypeName;
import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleTS extends TypeS {
  private final ImmutableList<TypeS> items;

  public TupleTS(List<? extends TypeS> items) {
    super(tupleTypeName(items), calculateVars(items));
    this.items = ImmutableList.copyOf(items);
  }

  private static VarSetS calculateVars(List<? extends TypeS> items) {
    return varSetS(items);
  }

  @Override
  public TupleTS mapComponents(Function<TypeS, TypeS> mapper) {
    return new TupleTS(map(items, mapper));
  }

  @Override
  public TupleTS mapVars(Function<VarS, TypeS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new TupleTS(map(items, t -> t.mapVars(varMapper)));
    }
  }

  public ImmutableList<TypeS> items() {
    return items;
  }

  public int size() {
    return items.size();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TupleTS that
        && items.equals(that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }
}

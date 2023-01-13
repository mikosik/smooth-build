package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.tupleTypeName;
import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;

import java.util.List;
import java.util.Objects;

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

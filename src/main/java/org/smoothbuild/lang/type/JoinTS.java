package org.smoothbuild.lang.type;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Sets.map;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Least upper bound (aka Join) of a set of slices.
 */
public final class JoinTS extends MergingTS {
  private final ImmutableSet<TypeS> elems;

  private JoinTS(ImmutableSet<TypeS> elems) {
    super(calculateName(elems), calculateVars(elems));
    this.elems = elems;
  }

  private static String calculateName(Set<TypeS> elems) {
    return elems.stream()
        .map(TypeS::name)
        .collect(joining(" ⊔ "));
  }

  public ImmutableSet<TypeS> elems() {
    return elems;
  }

  public static TypeS join(TypeS a, TypeS b) {
    if (isConstrTriviallyAllowed(a, b)) {
      return b;
    }
    if (isConstrTriviallyAllowed(b, a)) {
      return a;
    }

    Builder<TypeS> builder = ImmutableSet.builder();
    addElem(a, builder);
    addElem(b, builder);
    ImmutableSet<TypeS> elems = builder.build();
    if (elems.size() == 1) {
      return elems.iterator().next();
    } else {
      return new JoinTS(elems);
    }
  }

  private static void addElem(TypeS type, Builder<TypeS> result) {
    if (type instanceof JoinTS join) {
      result.addAll(join.elems);
    } else {
      result.add(type);
    }
  }

  @Override
  public TypeS withPrefixedVars(String prefix) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new JoinTS(map(elems, e -> e.withPrefixedVars(prefix)));
    }
  }

  @Override
  public TypeS removeVarPrefixes() {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new JoinTS(map(elems, TypeS::removeVarPrefixes));
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof JoinTS that
        && this.elems.equals(that.elems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elems.hashCode());
  }
}

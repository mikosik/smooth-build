package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Sets.map;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.Objects;
import java.util.Set;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Least upper bound (aka Join) of a set of slices.
 */
public final class JoinTS extends MergeTS {
  private JoinTS(ImmutableSet<TypeS> elems) {
    super(calculateName(elems), calculateVars(elems), elems);
  }

  private static String calculateName(Set<TypeS> elems) {
    return elems.stream()
        .map(TypeS::name)
        .collect(joining(" ⊔ "));
  }

  public static TypeS join(ImmutableSet<TypeS> elems) {
    return new JoinTS(elems);
  }

  public static TypeS joinReduced(Set<? extends TypeS> elems) {
    checkArgument(!elems.isEmpty(), "Elems must have at least one element.");
    Builder<TypeS> builder = ImmutableSet.builder();
    NothingTS nothing = null;
    for (TypeS elem : elems) {
      switch (elem) {
        case AnyTS any:
          return any;
        case NothingTS not:
          nothing = not;
          break;
        case JoinTS join:
          builder.addAll(join.elems());
          break;
        default:
          builder.add(elem);
      }
    }
    var reducedElems = builder.build();
    return switch (reducedElems.size()) {
      case 0 -> nothing;
      case 1 -> reducedElems.iterator().next();
      default -> new JoinTS(reducedElems);
    };
  }

  @Override
  public Side direction() {
    return UPPER;
  }

  @Override
  public TypeS withPrefixedVars(String prefix) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new JoinTS(map(elems(), e -> e.withPrefixedVars(prefix)));
    }
  }

  @Override
  public TypeS removeVarPrefixes() {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new JoinTS(map(elems(), TypeS::removeVarPrefixes));
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof JoinTS that
        && this.elems().equals(that.elems());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elems().hashCode());
  }
}

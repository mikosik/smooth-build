package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.type.Side.UPPER;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Sets.map;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Least upper bound (aka Join) of a set of slices.
 */
public final class JoinTS extends MergeTS {
  private JoinTS(ImmutableSet<MonoTS> elems) {
    super(calculateName(elems), varSetS(elems), elems);
  }

  private static String calculateName(Set<MonoTS> elems) {
    return elems.stream()
        .map(MonoTS::name)
        .collect(joining(" ⊔ "));
  }

  public static MonoTS join(ImmutableSet<MonoTS> elems) {
    return new JoinTS(elems);
  }

  public static MonoTS joinReduced(Set<? extends MonoTS> elems) {
    checkArgument(!elems.isEmpty(), "Elems must have at least one element.");
    Builder<MonoTS> builder = ImmutableSet.builder();
    NothingTS nothing = null;
    for (MonoTS elem : elems) {
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
  public boolean includes(MonoTS type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MonoTS mapVars(Function<VarS, VarS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new JoinTS(map(elems(), e -> e.mapVars(varMapper)));
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

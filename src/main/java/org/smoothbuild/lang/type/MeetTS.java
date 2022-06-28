package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.type.Side.LOWER;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Sets.map;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Greatest lower bound (aka Meet) of a set of slices.
 */
public final class MeetTS extends MergeTS {
  private MeetTS(ImmutableSet<MonoTS> elems) {
    super(calculateName(elems), varSetS(elems), elems);
  }

  private static String calculateName(Set<MonoTS> elems) {
    return elems.stream()
        .map(MonoTS::name)
        .collect(joining(" ⊓ "));
  }

  public static MonoTS meet(ImmutableSet<MonoTS> elems) {
    return new MeetTS(elems);
  }

  public static MonoTS meetReduced(Set<? extends MonoTS> elems) {
    checkArgument(!elems.isEmpty(), "Elems must have at least one element.");
    Builder<MonoTS> builder = ImmutableSet.builder();
    AnyTS any = null;
    for (MonoTS elem : elems) {
      switch (elem) {
        case NothingTS nothing:
          return nothing;
        case AnyTS any_:
          any = any_;
          break;
        case MeetTS meet:
          builder.addAll(meet.elems());
          break;
        default:
          builder.add(elem);
      }
    }
    var reducedElems = builder.build();
    return switch (reducedElems.size()) {
      case 0 -> any;
      case 1 -> reducedElems.iterator().next();
      default -> new MeetTS(reducedElems);
    };
  }

  @Override
  public Side direction() {
    return LOWER;
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
      return new MeetTS(map(elems(), e -> e.mapVars(varMapper)));
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof MeetTS that
        && this.elems().equals(that.elems());
  }

  @Override
  public int hashCode() {
    return Objects.hash(elems().hashCode());
  }
}

package org.smoothbuild.lang.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Sets.map;
import static org.smoothbuild.util.type.Side.LOWER;

import java.util.Objects;
import java.util.Set;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Greatest lower bound (aka Meet) of a set of slices.
 */
public final class MeetTS extends MergeTS {
  private MeetTS(ImmutableSet<TypeS> elems) {
    super(calculateName(elems), calculateVars(elems), elems);
  }

  private static String calculateName(Set<TypeS> elems) {
    return elems.stream()
        .map(TypeS::name)
        .collect(joining(" ⊓ "));
  }

  public static TypeS meet(ImmutableSet<TypeS> elems) {
    return new MeetTS(elems);
  }

  public static TypeS meetReduced(Set<? extends TypeS> elems) {
    checkArgument(!elems.isEmpty(), "Elems must have at least one element.");
    Builder<TypeS> builder = ImmutableSet.builder();
    AnyTS any = null;
    for (TypeS elem : elems) {
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
  public TypeS withPrefixedVars(String prefix) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new MeetTS(map(elems(), e -> e.withPrefixedVars(prefix)));
    }
  }

  @Override
  public TypeS removeVarPrefixes() {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new MeetTS(map(elems(), TypeS::removeVarPrefixes));
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

package org.smoothbuild.lang.type;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.lang.type.VarSetS.varSetS;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Greatest lower bound (aka Meet) of a set of slices.
 */
public final class MeetTS extends MergingTS {
  private final ImmutableSet<TypeS> elems;

  private MeetTS(ImmutableSet<TypeS> elems) {
    super("Meet", varSetS());
    this.elems = elems;
  }

  public ImmutableSet<TypeS> elems() {
    return elems;
  }

  public static TypeS meet(TypeS a, TypeS b) {
    if (isConstrTriviallyAllowed(a, b)) {
      return a;
    }
    if (isConstrTriviallyAllowed(b, a)) {
      return b;
    }

    Builder<TypeS> builder = ImmutableSet.builder();
    addElem(a, builder);
    addElem(b, builder);
    ImmutableSet<TypeS> elems = builder.build();
    if (elems.size() == 1) {
      return elems.iterator().next();
    } else {
      return new MeetTS(elems);
    }
  }

  private static void addElem(TypeS type, Builder<TypeS> result) {
    if (type instanceof MeetTS meet) {
      result.addAll(meet.elems);
    } else {
      result.add(type);
    }
  }

  @Override
  public String toString() {
    return elems.stream()
        .map(Object::toString)
        .collect(joining(" ⊓ "));
  }
}

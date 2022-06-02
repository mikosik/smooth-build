package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.JoinTS.joinReduced;
import static org.smoothbuild.lang.type.MeetTS.meetReduced;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableSet;

public abstract sealed class MergingTS extends TypeS permits JoinTS, MeetTS {
  private final ImmutableSet<TypeS> elems;

  protected MergingTS(String name, VarSetS vars, ImmutableSet<TypeS> elems) {
    super(name, vars);
    this.elems = elems;
  }

  public ImmutableSet<TypeS> elems() {
    return elems;
  }

  public static TypeS mergeReduced(TypeS a, TypeS b, Side direction) {
    var elems = set(a, b);
    return switch (direction) {
      case UPPER -> joinReduced(elems);
      case LOWER -> meetReduced(elems);
    };
  }
}

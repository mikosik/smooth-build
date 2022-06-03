package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.JoinTS.join;
import static org.smoothbuild.lang.type.JoinTS.joinReduced;
import static org.smoothbuild.lang.type.MeetTS.meet;
import static org.smoothbuild.lang.type.MeetTS.meetReduced;

import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableSet;

public abstract sealed class MergeTS extends TypeS permits JoinTS, MeetTS {
  private final ImmutableSet<TypeS> elems;

  protected MergeTS(String name, VarSetS vars, ImmutableSet<TypeS> elems) {
    super(name, vars);
    this.elems = elems;
  }

  public ImmutableSet<TypeS> elems() {
    return elems;
  }

  public abstract Side direction();

  public static TypeS merge(ImmutableSet<TypeS> elems, Side direction) {
    return switch (direction) {
      case UPPER -> join(elems);
      case LOWER -> meet(elems);
    };
  }

  public static TypeS mergeReduced(ImmutableSet<TypeS> elems, Side direction) {
    return switch (direction) {
      case UPPER -> joinReduced(elems);
      case LOWER -> meetReduced(elems);
    };
  }
}

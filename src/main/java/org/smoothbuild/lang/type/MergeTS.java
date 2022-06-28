package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.JoinTS.join;
import static org.smoothbuild.lang.type.JoinTS.joinReduced;
import static org.smoothbuild.lang.type.MeetTS.meet;
import static org.smoothbuild.lang.type.MeetTS.meetReduced;

import com.google.common.collect.ImmutableSet;

public abstract sealed class MergeTS extends MonoTS permits JoinTS, MeetTS {
  private final ImmutableSet<MonoTS> elems;

  protected MergeTS(String name, VarSetS vars, ImmutableSet<MonoTS> elems) {
    super(name, vars);
    this.elems = elems;
  }

  public ImmutableSet<MonoTS> elems() {
    return elems;
  }

  public abstract Side direction();

  public static MonoTS merge(ImmutableSet<MonoTS> elems, Side direction) {
    return switch (direction) {
      case UPPER -> join(elems);
      case LOWER -> meet(elems);
    };
  }

  public static MonoTS mergeReduced(ImmutableSet<MonoTS> elems, Side direction) {
    return switch (direction) {
      case UPPER -> joinReduced(elems);
      case LOWER -> meetReduced(elems);
    };
  }
}

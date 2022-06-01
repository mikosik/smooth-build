package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.JoinTS.joinReduced;
import static org.smoothbuild.lang.type.MeetTS.meetReduced;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.util.type.Side;

public abstract sealed class MergingTS extends TypeS permits JoinTS, MeetTS {
  protected MergingTS(String name, VarSetS vars) {
    super(name, vars);
  }

  public static TypeS mergeReduced(TypeS a, TypeS b, Side direction) {
    var elems = set(a, b);
    return switch (direction) {
      case UPPER -> joinReduced(elems);
      case LOWER -> meetReduced(elems);
    };
  }
}

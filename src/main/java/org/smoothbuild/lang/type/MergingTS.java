package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.JoinTS.join;
import static org.smoothbuild.lang.type.MeetTS.meet;

import org.smoothbuild.util.type.Side;

public abstract sealed class MergingTS extends TypeS permits JoinTS, MeetTS {
  protected MergingTS(String name, VarSetS vars) {
    super(name, vars);
  }

  public static TypeS merge(TypeS a, TypeS b, Side direction) {
    return switch (direction) {
      case UPPER -> join(a, b);
      case LOWER -> meet(a, b);
    };
  }
}

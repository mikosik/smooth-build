package org.smoothbuild.lang.function.def.args;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.function.base.Param;

public class Assignment {
  private final Param param;
  private final Argument argument;

  public static Assignment assignment(Param param, Argument argument) {
    return new Assignment(param, argument);
  }

  private Assignment(Param param, Argument argument) {
    boolean isAssignable = Conversions.canAssign(argument.type(), param.type());
    if (!isAssignable) {
      throw new IllegalArgumentException("Param " + param + " cannot be assigned from " + argument
          + " argument.");
    }
    this.param = checkNotNull(param);
    this.argument = checkNotNull(argument);
  }

  public Param param() {
    return param;
  }

  public Argument argument() {
    return argument;
  }

  public String assignedName() {
    return param.name();
  }
}

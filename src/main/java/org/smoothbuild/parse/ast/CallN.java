package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.define.Loc;

import com.google.common.collect.ImmutableList;

public final class CallN extends ExprN {
  private final ExprN callable;
  private final List<ArgN> args;
  private ImmutableList<ArgN> assignedArgs;

  public CallN(ExprN callable, List<ArgN> args, Loc loc) {
    super(loc);
    this.callable = callable;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprN callable() {
    return callable;
  }

  public List<ArgN> args() {
    return args;
  }

  public void setAssignedArgs(ImmutableList<ArgN> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of args containing both explicit and default arguments.
   */
  public ImmutableList<ArgN> assignedArgs() {
    return assignedArgs;
  }
}

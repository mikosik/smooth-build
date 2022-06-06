package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class CallN extends ExprN {
  private final ObjN callee;
  private final List<ArgN> args;
  private ImmutableList<ArgN> assignedArgs;

  public CallN(ObjN callee, List<ArgN> args, Loc loc) {
    super(loc);
    this.callee = callee;
    this.args = ImmutableList.copyOf(args);
  }

  public ObjN callee() {
    return callee;
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

package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class CallP extends MonoP implements MonoExprP {
  private final ObjP callee;
  private final List<ArgP> args;
  private ImmutableList<ArgP> assignedArgs;

  public CallP(ObjP callee, List<ArgP> args, Loc loc) {
    super(loc);
    this.callee = callee;
    this.args = ImmutableList.copyOf(args);
  }

  public ObjP callee() {
    return callee;
  }

  public List<ArgP> args() {
    return args;
  }

  public void setAssignedArgs(ImmutableList<ArgP> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of args containing both explicit and default arguments.
   */
  public ImmutableList<ArgP> assignedArgs() {
    return assignedArgs;
  }
}

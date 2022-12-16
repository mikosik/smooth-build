package org.smoothbuild.compile.ps.ast.expr;

import java.util.List;

import org.smoothbuild.compile.lang.base.location.Location;

import com.google.common.collect.ImmutableList;

public final class CallP extends ExprP {
  private final ExprP callee;
  private final ImmutableList<ExprP> args;
  private ImmutableList<ExprP> positionedArgs;

  public CallP(ExprP callee, List<ExprP> args, Location location) {
    super(location);
    this.callee = callee;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprP callee() {
    return callee;
  }

  public ImmutableList<ExprP> args() {
    return args;
  }

  public void setPositionedArgs(ImmutableList<ExprP> positionedArgs) {
    this.positionedArgs = positionedArgs;
  }

  /**
   * @return List of args where position of argument in the list
   * matches position of parameter to which that arg is assigned.
   * Size of list is equal to callee parameter list size.
   * While {@link #args()} holds arguments expressions as they
   * were ordered in source code where NamedArgP represents
   * explicit parameter name assignment, this method has argument
   * list that has been processed two ways:
   * 1. All NamedArgP are replaced by #{@link NamedArgP#expr()}
   * and placed at position denoted by #{@link NamedArgP#name()}.
   * 2. Missing args are replaced with RefP pointing
   * to that parameter default value.
   */
  public ImmutableList<ExprP> positionedArgs() {
    return positionedArgs;
  }
}

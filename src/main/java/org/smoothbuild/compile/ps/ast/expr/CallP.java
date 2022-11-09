package org.smoothbuild.compile.ps.ast.expr;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class CallP extends ExprP {
  private final ExprP callee;
  private final List<ExprP> args;
  private Optional<ImmutableList<ExprP>> positionedArgs;

  public CallP(ExprP callee, List<ExprP> args, Loc loc) {
    super(loc);
    this.callee = callee;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprP callee() {
    return callee;
  }

  public List<ExprP> args() {
    return args;
  }

  public void setPositionedArgs(Optional<ImmutableList<ExprP>> positionedArgs) {
    this.positionedArgs = positionedArgs;
  }

  /**
   * @return List of args where position of arg in the list matches position of parameter to which
   * that arg is assigned. NamedArgP are assigned to parameters at proper positions.
   * Missing args are replaced with DefaultArgP.
   * Size of list is equal to callee parameter list size.
   */
  public Optional<ImmutableList<ExprP>> positionedArgs() {
    return positionedArgs;
  }
}

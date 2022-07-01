package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;

import com.google.common.collect.ImmutableList;

public final class CallP extends MonoP implements MonoExprP {
  private final ObjP callee;
  private final List<ArgP> args;
  private ImmutableList<Optional<ArgP>> explicitArgs;

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

  public void setExplicitArgs(ImmutableList<Optional<ArgP>> explicitArgs) {
    this.explicitArgs = explicitArgs;
  }

  /**
   * @return List of explicit args where position of arg in the list matches parameter to which
   * that arg is assigned. Named arguments are assigned to parameters at proper positions.
   * Optional.empty() signals that for that parameter its default argument should be used,
   * and it was already verified that this parameter has default argument.
   * Size of list is equal to callee parameter list size.
   */
  public ImmutableList<Optional<ArgP>> explicitArgs() {
    return explicitArgs;
  }
}

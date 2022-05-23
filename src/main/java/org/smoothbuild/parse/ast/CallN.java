package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.define.Loc;

import com.google.common.collect.ImmutableList;

public final class CallN extends ExprN {
  private final ExprN callable;
  private final List<ArgN> args;
  private List<Optional<ArgN>> assignedArgs;

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

  public void setAssignedArgs(List<Optional<ArgN>> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of args where position of arg specifies to which parameter that
   * arg has been assigned. Optional.empty() value means that given parameter has no
   * arg assigned explicitly and parameter's default arg should be used.
   */
  public List<Optional<ArgN>> assignedArgs() {
    return assignedArgs;
  }
}

package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public final class CallN extends ExprN {
  private final ExprN func;
  private final List<ArgNode> args;
  private List<Optional<ArgNode>> assignedArgs;

  public CallN(ExprN func, List<ArgNode> args, Location location) {
    super(location);
    this.func = func;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprN func() {
    return func;
  }

  public List<ArgNode> args() {
    return args;
  }

  public void setAssignedArgs(List<Optional<ArgNode>> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of args where position of arg specifies to which parameter that
   * arg has been assigned. Optional.empty() value means that given parameter has no
   * arg assigned explicitly and parameter's default arg should be used.
   */
  public List<Optional<ArgNode>> assignedArgs() {
    return assignedArgs;
  }
}
package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public final class CallN extends ExprN {
  private final ExprN function;
  private final List<ArgNode> args;
  private List<Optional<ArgNode>> assignedArgs;

  public CallN(ExprN function, List<ArgNode> args, Location location) {
    super(location);
    this.function = function;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprN function() {
    return function;
  }

  public List<ArgNode> args() {
    return args;
  }

  public void setAssignedArgs(List<Optional<ArgNode>> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of arguments where position of argument specifies to which parameter that
   * argument has been assigned. Optional.empty() value means that given parameter has no
   * argument assigned explicitly and parameter's default argument should be used.
   */
  public List<Optional<ArgNode>> assignedArgs() {
    return assignedArgs;
  }
}

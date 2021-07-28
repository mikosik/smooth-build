package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  private final ExprNode function;
  private final List<ArgNode> args;
  private List<Optional<ArgNode>> assignedArgs;

  public CallNode(ExprNode function, List<ArgNode> args, Location location) {
    super(location);
    this.function = function;
    this.args = ImmutableList.copyOf(args);
  }

  public ExprNode function() {
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
   * argument assigned and parameter's default value should be used.
   */
  public List<Optional<ArgNode>> assignedArgs() {
    return assignedArgs;
  }
}

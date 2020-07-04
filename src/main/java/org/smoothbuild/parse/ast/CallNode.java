package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  private final String name;
  private final List<ArgNode> args;
  private List<ArgNode> assignedArgs;

  public CallNode(String name, List<ArgNode> args, Location location) {
    super(location);
    this.name = name;
    this.args = ImmutableList.copyOf(args);
  }

  public String calledName() {
    return name;
  }

  public List<ArgNode> args() {
    return args;
  }

  public void setAssignedArgs(List<ArgNode> sortedArgs) {
    this.assignedArgs = sortedArgs;
  }

  /**
   * @return List of arguments where position of argument specifies to which parameter that
   * argument has been assigned. Null element means that function call didn't specify
   * assignment for given parameters.
   */
  public List<ArgNode> assignedArgs() {
    return assignedArgs;
  }
}

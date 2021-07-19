package org.smoothbuild.lang.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  private final RefNode ref;
  private final List<ArgNode> args;
  private List<ArgNode> assignedArgs;

  public CallNode(RefNode ref, List<ArgNode> args, Location location) {
    super(location);
    this.ref = ref;
    this.args = ImmutableList.copyOf(args);
  }

  public RefNode ref() {
    return ref;
  }

  public RefNode called() {
    return ref;
  }

  public List<ArgNode> args() {
    return args;
  }

  public void setAssignedArgs(List<ArgNode> assignedArgs) {
    this.assignedArgs = assignedArgs;
  }

  /**
   * @return List of arguments where position of argument specifies to which parameter that
   * argument has been assigned. Null element means that function call didn't specify
   * assignment for given parameter.
   */
  public List<ArgNode> assignedArgs() {
    return assignedArgs;
  }
}

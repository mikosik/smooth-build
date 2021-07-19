package org.smoothbuild.lang.parse.ast;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  private final RefNode ref;
  private final List<ArgNode> args;
  private List<Optional<ArgNode>> assignedArgs;

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

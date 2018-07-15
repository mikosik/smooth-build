package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode implements Named {
  private final String name;
  private final List<ArgNode> args;

  public CallNode(String name, List<ArgNode> args, Location location) {
    super(location);
    this.name = name;
    this.args = ImmutableList.copyOf(args);
  }

  @Override
  public String name() {
    return name.toString();
  }

  public List<ArgNode> args() {
    return args;
  }
}

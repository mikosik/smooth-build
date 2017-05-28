package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  private final Name name;
  private final List<ArgNode> args;

  public CallNode(Name name, List<ArgNode> args, CodeLocation codeLocation) {
    super(codeLocation);
    this.name = name;
    this.args = ImmutableList.copyOf(args);
  }

  public Name name() {
    return name;
  }

  public List<ArgNode> args() {
    return args;
  }
}

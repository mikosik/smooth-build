package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;

import com.google.common.collect.ImmutableList;

public class CallNode extends ExprNode {
  public static final class ParamRefFlag {}

  private final Name name;
  private final List<ArgNode> args;
  private final boolean hasParentheses;

  public CallNode(Name name, List<ArgNode> args, boolean hasParentheses, Location location) {
    super(location);
    this.name = name;
    this.hasParentheses = hasParentheses;
    this.args = ImmutableList.copyOf(args);
  }

  public Name name() {
    return name;
  }

  public List<ArgNode> args() {
    return args;
  }

  public boolean hasParentheses() {
    return hasParentheses;
  }
}

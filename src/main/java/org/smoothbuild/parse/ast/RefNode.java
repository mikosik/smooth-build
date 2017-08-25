package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

public class RefNode extends ExprNode {
  private final Name name;

  public RefNode(Name name, CodeLocation codeLocation) {
    super(codeLocation);
    this.name = name;
  }

  public Name name() {
    return name;
  }
}

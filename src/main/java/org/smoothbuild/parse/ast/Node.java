package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.message.CodeLocation;

public class Node {
  private final CodeLocation codeLocation;

  public Node(CodeLocation codeLocation) {
    this.codeLocation = codeLocation;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }
}

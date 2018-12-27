package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.type.Type;


public class ArgNode extends NamedNode {
  private final int position;
  private final ExprNode expr;

  public ArgNode(int position, String name, ExprNode expr, Location location) {
    super(name, location);
    this.position = position;
    this.expr = expr;
  }

  public int position() {
    return position;
  }

  public boolean hasName() {
    return super.name() != null;
  }

  @Override
  public String name() {
    checkState(hasName());
    return super.name();
  }

  public String nameSanitized() {
    return hasName() ? name() : "<nameless>";
  }

  public String typeAndName() {
    return get(Type.class).name() + ":" + nameSanitized();
  }

  public ExprNode expr() {
    return expr;
  }
}

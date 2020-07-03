package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.Type;

public class ArgNode extends NamedNode {
  private final ExprNode expr;

  public ArgNode(String name, ExprNode expr, Location location) {
    super(name, location);
    this.expr = expr;
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
    return type().map(Type::name).orElse("<missing type>") + ":" + nameSanitized();
  }

  public ExprNode expr() {
    return expr;
  }
}

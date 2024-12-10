package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.location.Location;

public final class PCall extends PExpr {
  private final PExpr callee;
  private final List<PExpr> args;
  private List<PExpr> positionedArgs;

  public PCall(PExpr callee, List<PExpr> args, Location location) {
    super(location);
    this.callee = callee;
    this.args = args;
  }

  public PExpr callee() {
    return callee;
  }

  public List<PExpr> args() {
    return args;
  }

  public void setPositionedArgs(List<PExpr> positionedArgs) {
    this.positionedArgs = positionedArgs;
  }

  /**
   * @return List of args where position of argument in the list
   * matches position of parameter to which that arg is assigned.
   * Size of list is equal to callee parameter list size.
   * While {@link #args()} holds arguments expressions as they
   * were ordered in source code where PNamedArg represents
   * explicit parameter name assignment, this method has argument
   * list that has been processed two ways:
   * 1. All PNamedArg are replaced by #{@link PNamedArg#expr()}
   * and placed at position denoted by #{@link PNamedArg#name()}.
   * 2. Missing args are replaced with RefP pointing
   * to that parameter default value.
   */
  public List<PExpr> positionedArgs() {
    return positionedArgs;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PCall that
        && Objects.equals(this.callee, that.callee)
        && Objects.equals(this.args(), that.args())
        && Objects.equals(this.location(), that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(callee, args, location());
  }

  @Override
  public String toString() {
    var fields =
        list("callee = " + callee, "args = " + args, "location = " + location()).toString("\n");
    return "PCall(\n" + indent(fields) + "\n)";
  }
}

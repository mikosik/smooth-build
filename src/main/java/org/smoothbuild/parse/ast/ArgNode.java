package org.smoothbuild.parse.ast;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.Ordering;

public class ArgNode extends Node {
  private final int position;
  private final Name name;
  private final ExprNode expr;

  public ArgNode(int position, Name name, ExprNode expr, CodeLocation codeLocation) {
    super(codeLocation);
    this.position = position;
    this.name = name;
    this.expr = expr;
  }

  public int position() {
    return position;
  }

  public boolean hasName() {
    return name != null;
  }

  public Name name() {
    checkState(hasName());
    return name;
  }

  public String nameSanitized() {
    return hasName() ? name().toString() : "<nameless>";
  }

  public String typeAndName() {
    return get(Type.class).name() + ":" + nameSanitized();
  }

  public ExprNode expr() {
    return expr;
  }

  public String toPaddedString(int minTypeLength, int minNameLength, int minPositionLength) {
    String type = padEnd(get(Type.class).name(), minTypeLength, ' ') + ": ";
    String name = padEnd(nameSanitized(), minNameLength, ' ');
    String position = padEnd(positionString(), minPositionLength, ' ');
    String location = codeLocation().toString();
    return type + name + " #" + position + " " + location;
  }

  private String positionString() {
    return position() == 0 ? "|" : Integer.toString(position());
  }

  public static final Ordering<ArgNode> POSITION_ORDERING = new Ordering<ArgNode>() {
    public int compare(ArgNode argument1, ArgNode argument2) {
      int position1 = argument1.position();
      int position2 = argument2.position();
      if (position1 == position2) {
        return 0;
      }
      if (position1 < position2) {
        return -1;
      } else {
        return 1;
      }
    }
  };
}

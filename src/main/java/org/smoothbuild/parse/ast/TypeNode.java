package org.smoothbuild.parse.ast;

import static java.lang.Character.isLowerCase;

import org.smoothbuild.lang.message.Location;

public class TypeNode extends NamedNode {
  public TypeNode(String name, Location location) {
    super(name, location);
  }

  public static boolean isGenericName(String name) {
    return 0 < name.length() && isLowerCase(name.charAt(0));
  }
}

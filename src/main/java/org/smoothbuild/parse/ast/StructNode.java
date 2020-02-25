package org.smoothbuild.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class StructNode extends ParameterizedNode {
  private final NamedNode constructor;
  private final List<FieldNode> fields;

  public StructNode(String name, List<FieldNode> fields, Location location) {
    super(name, location);
    this.constructor = new NamedNode(UPPER_CAMEL.to(LOWER_CAMEL, name), location);
    this.fields = ImmutableList.copyOf(fields);
  }

  public Named constructor() {
    return constructor;
  }

  public List<FieldNode> fields() {
    return fields;
  }

  public static String constructorNameToTypeName(String constructorName) {
    return LOWER_CAMEL.to(UPPER_CAMEL, constructorName);
  }
}

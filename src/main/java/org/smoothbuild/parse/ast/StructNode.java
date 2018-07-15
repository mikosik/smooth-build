package org.smoothbuild.parse.ast;

import java.util.List;

import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final List<FieldNode> fields;

  public StructNode(String name, List<FieldNode> fields, Location location) {
    super(name, location);
    this.fields = ImmutableList.copyOf(fields);
  }

  public List<FieldNode> fields() {
    return fields;
  }
}

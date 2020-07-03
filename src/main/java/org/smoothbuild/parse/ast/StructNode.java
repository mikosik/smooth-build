package org.smoothbuild.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.Type;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final ParameterizedNode constructor;
  private final List<FieldNode> fields;

  public StructNode(String name, List<FieldNode> fields, Location location) {
    super(name, location);
    this.constructor = new ConstructorNode(name, location);
    this.fields = ImmutableList.copyOf(fields);
  }

  public ParameterizedNode constructor() {
    return constructor;
  }

  public List<FieldNode> fields() {
    return fields;
  }

  private class ConstructorNode extends ParameterizedNode {
    public ConstructorNode(String structName, Location location) {
      super(UPPER_CAMEL.to(LOWER_CAMEL, structName), location);
    }

    @Override
    public Optional<Type> type() {
      return StructNode.this.type();
    }

    @Override
    public void setType(Optional<Type> type) {
      throw new UnsupportedOperationException();
    }
  }
}

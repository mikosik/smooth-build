package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final ConstructorNode constructor;
  private final ImmutableList<ItemNode> fields;

  public StructNode(String name, List<ItemNode> fields, Location location) {
    this(name, ImmutableList.copyOf(fields), location);
  }

  private StructNode(String name, ImmutableList<ItemNode> fields, Location location) {
    super(name, location);
    this.constructor = new ConstructorNode(name, fields, location);
    this.fields = fields;
  }

  public ConstructorNode constructor() {
    return constructor;
  }

  public List<ItemNode> fields() {
    return fields;
  }

  public class ConstructorNode extends FunctionNode {
    public ConstructorNode(String structName, List<ItemNode> params, Location location) {
      super(
          Optional.of(new TypeNode(structName, location)),
          UPPER_CAMEL.to(LOWER_CAMEL, structName),
          Optional.empty(),
          params,
          Optional.empty(),
          location);
    }

    @Override
    public ImmutableList<ItemNode> params() {
      return fields;
    }
  }
}

package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.NamedList.namedListWithDuplicates;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final NamedList<ItemNode> fields;
  private final ConstructorNode constructor;

  public StructNode(String name, List<ItemNode> fields, Location location) {
    this(name, namedListWithDuplicates(ImmutableList.copyOf(fields)), location);
  }

  private StructNode(String name, NamedList<ItemNode> fields, Location location) {
    super(name, location);
    this.fields = fields;
    this.constructor = new ConstructorNode(name, fields, location);
  }

  public ConstructorNode constructor() {
    return constructor;
  }

  public NamedList<ItemNode> fields() {
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
    public NamedList<ItemNode> params() {
      return fields;
    }
  }
}

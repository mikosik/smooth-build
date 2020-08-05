package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final ConstructorNode constructor;
  private final List<ItemNode> fields;

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

  public class ConstructorNode extends CallableNode {
    public ConstructorNode(String structName, List<ItemNode> params, Location location) {
      super(new TypeNode(structName, location), UPPER_CAMEL.to(LOWER_CAMEL, structName),
          null, params, location);
    }

    @Override
    public Optional<Type> type() {
      return StructNode.this.type();
    }

    @Override
    public void setType(Optional<Type> type) {
      throw new UnsupportedOperationException();
    }

    public void initializeParameterInfos() {
      StructType type = (StructType) StructNode.this.type().get();
      var parameterInfos = type.fields()
          .values()
          .stream()
          .map(f -> new Parameter(f.index(), f.type(), f.name(), null, f.location()))
          .collect(toImmutableList());
      setParameterInfos(parameterInfos);
    }
  }
}

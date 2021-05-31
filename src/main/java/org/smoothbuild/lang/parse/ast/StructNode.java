package org.smoothbuild.lang.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.lang.base.type.Types.function;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Struct;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

public class StructNode extends NamedNode {
  private final ConstructorNode constructor;
  private final List<ItemNode> fields;
  private Optional<Struct> struct;

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

  public Optional<Struct> struct() {
    return struct;
  }

  public void setStruct(Optional<Struct> struct) {
    this.struct = struct;
    setType(struct.map(Defined::type));
  }

  public class ConstructorNode extends CallableNode {
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
    public Optional<Type> type() {
      Optional<Type> type = StructNode.this.type();
      if (type.isPresent() && eachFieldSignatureIsPresent()) {
        return Optional.of(
            function(type.get(), map(fields, itemNode -> itemNode.itemSignature().get())));
      } else {
        return Optional.empty();
      }
    }

    @Override
    public void setType(Optional<Type> type) {
      throw new UnsupportedOperationException();
    }
  }

  private boolean eachFieldSignatureIsPresent() {
    return fields.stream().allMatch(f -> f.itemSignature().isPresent());
  }
}

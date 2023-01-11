package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.interfaceTypeName;

import java.util.function.Function;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public final class StructTS extends FieldSetTS {
  private final NList<ItemSigS> fields;

  public StructTS(String name, NList<ItemSigS> fields) {
    super(name, calculateFieldSetVars(fields));
    this.fields = fields;
  }

  public NList<ItemSigS> fields() {
    return fields;
  }

  @Override
  public StructTS mapComponents(Function<TypeS, TypeS> mapper) {
    return new StructTS(name(), fields.map(f -> f.mapType(mapper)));
  }

  @Override
  public StructTS mapVars(Function<VarS, TypeS> varMapper) {
    if (vars().isEmpty()) {
      return this;
    } else {
      return new StructTS(name(), fields.map(f -> f.mapVarsInType(varMapper)));
    }
  }

  @Override
  public ImmutableMap<String, ItemSigS> fieldSet() {
    return fields.map();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof StructTS thatStruct
        && this.name().equals(thatStruct.name())
        && this.fields.equals(thatStruct.fields);
  }

  @Override
  public String toString() {
    return name() + interfaceTypeName(fieldSet());
  }
}

package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.interfaceTypeName;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.function.Function;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;

import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public final class InterfaceTS extends FieldSetTS {
  private final ImmutableMap<String, ItemSigS> fields;

  public InterfaceTS(ImmutableMap<String, ItemSigS> fields) {
    super(interfaceTypeName(fields));
    this.fields = fields;
  }

  @Override
  public ImmutableMap<String, ItemSigS> fieldSet() {
    return fields;
  }

  @Override
  public InterfaceTS mapComponents(Function<TypeS, TypeS> mapper) {
    return new InterfaceTS(mapValues(fields, f -> f.mapType(mapper)));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof InterfaceTS that
        && this.fields.equals(that.fields);
  }
}

package org.smoothbuild.compile.fs.lang.type;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.interfaceTypeName;

import org.smoothbuild.compile.fs.lang.define.ItemSigS;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * This class is immutable.
 */
public final class InterfaceTS extends FieldSetTS {
  private final ImmutableSortedMap<String, ItemSigS> fields;

  public InterfaceTS(ImmutableMap<String, ItemSigS> fields) {
    super(interfaceTypeName(fields), calculateFieldSetVars(fields.values()));
    this.fields = ImmutableSortedMap.copyOf(fields);
  }

  @Override
  public ImmutableSortedMap<String, ItemSigS> fieldSet() {
    return fields;
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

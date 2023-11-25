package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.compile.frontend.lang.base.TypeNamesS.interfaceTypeName;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.smoothbuild.compile.frontend.lang.define.ItemSigS;

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
    return object instanceof InterfaceTS that && this.fields.equals(that.fields);
  }
}
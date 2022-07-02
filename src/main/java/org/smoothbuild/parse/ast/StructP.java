package org.smoothbuild.parse.ast;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructP extends MonoNamedP {
  private final NList<ItemP> fields;
  private final FuncP ctor;

  public StructP(String name, List<ItemP> fields, Loc loc) {
    this(name, nlistWithNonUniqueNames(ImmutableList.copyOf(fields)), loc);
  }

  private StructP(String name, NList<ItemP> fields, Loc loc) {
    super(name, loc);
    this.fields = fields;
    this.ctor = new FuncP(
        Optional.of(new TypeP(name, loc)),
        UPPER_CAMEL.to(LOWER_CAMEL, name), fields, Optional.empty(), Optional.empty(),
        loc);
  }

  public FuncP ctor() {
    return ctor;
  }

  public NList<ItemP> fields() {
    return fields;
  }
}

package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.compile.lang.base.ValidNamesS.structNameToCtorName;
import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructP extends NalImpl implements WithScopeP {
  private final NList<ItemP> fields;
  private final NamedFuncP constructor;
  private ScopeP scope;

  public StructP(String name, List<ItemP> fields, Location location) {
    this(name, nlistWithShadowing(ImmutableList.copyOf(fields)), location);
  }

  private StructP(String name, NList<ItemP> fields, Location location) {
    super(name, location);
    this.fields = fields;
    this.constructor = new NamedFuncP(
        Optional.of(new TypeP(name, location)),
        structNameToCtorName(name),
        fields,
        Optional.empty(),
        Optional.empty(),
        location);
  }

  public NList<ItemP> fields() {
    return fields;
  }

  public NamedFuncP constructor() {
    return constructor;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
  }
}

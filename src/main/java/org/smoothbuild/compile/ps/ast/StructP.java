package org.smoothbuild.compile.ps.ast;

import static org.smoothbuild.compile.lang.type.TNamesS.structNameToCtorName;
import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.NalImpl;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public final class StructP extends NalImpl {
  private final NList<ItemP> fields;
  private final FuncP ctor;

  public StructP(String name, List<ItemP> fields, Loc loc) {
    this(name, nlistWithNonUniqueNames(ImmutableList.copyOf(fields)), loc);
  }

  private StructP(String name, NList<ItemP> fields, Loc loc) {
    super(name, loc);
    this.fields = fields;
    this.ctor = new FuncP(Optional.of(new TypeP(name, loc)), structNameToCtorName(name), fields,
        Optional.empty(), Optional.empty(), loc);
  }

  public FuncP ctor() {
    return ctor;
  }

  public NList<ItemP> fields() {
    return fields;
  }
}
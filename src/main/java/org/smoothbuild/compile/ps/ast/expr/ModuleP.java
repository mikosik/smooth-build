package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import java.util.List;

import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ModuleP {
  private final NList<StructP> structs;
  private final ImmutableList<NamedEvaluableP> evaluables;

  public ModuleP(List<StructP> structs, List<NamedEvaluableP> evaluables) {
    this.structs = nlistWithShadowing(ImmutableList.copyOf(structs));
    this.evaluables = ImmutableList.copyOf(evaluables);
  }

  public ImmutableList<NamedEvaluableP> evaluables() {
    return evaluables;
  }

  public NList<StructP> structs() {
    return structs;
  }
}

package org.smoothbuild.compile.ps.ast;

import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;

import org.smoothbuild.compile.ps.ast.refable.PolyEvaluableP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructP> structs;
  private final ImmutableList<PolyEvaluableP> evaluables;

  public Ast(List<StructP> structs, List<PolyEvaluableP> evaluables) {
    this.structs = nlistWithNonUniqueNames(ImmutableList.copyOf(structs));
    this.evaluables = ImmutableList.copyOf(evaluables);
  }

  public ImmutableList<PolyEvaluableP> evaluables() {
    return evaluables;
  }

  public NList<StructP> structs() {
    return structs;
  }
}

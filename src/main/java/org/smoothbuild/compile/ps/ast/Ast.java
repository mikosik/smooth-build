package org.smoothbuild.compile.ps.ast;

import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;

import org.smoothbuild.compile.ps.ast.refable.EvaluableP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructP> structs;
  private final ImmutableList<EvaluableP> evaluables;

  public Ast(List<StructP> structs, List<EvaluableP> evaluables) {
    this.structs = nlistWithNonUniqueNames(ImmutableList.copyOf(structs));
    this.evaluables = ImmutableList.copyOf(evaluables);
  }

  public ImmutableList<EvaluableP> evaluables() {
    return evaluables;
  }

  public NList<StructP> structs() {
    return structs;
  }
}

package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.collect.NList.nlistWithNonUniqueNames;

import java.util.List;

import org.smoothbuild.parse.ast.refable.PolyRefableP;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructP> structs;
  private final ImmutableList<PolyRefableP> refables;

  public Ast(List<StructP> structs, List<PolyRefableP> refables) {
    this.structs = nlistWithNonUniqueNames(ImmutableList.copyOf(structs));
    this.refables = ImmutableList.copyOf(refables);
  }

  public ImmutableList<PolyRefableP> refables() {
    return refables;
  }

  public NList<StructP> structs() {
    return structs;
  }
}

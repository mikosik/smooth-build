package org.smoothbuild.parse.ast;

import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;

import java.util.List;

import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructP> structs;
  private final ImmutableList<TopRefableP> topRefables;

  public Ast(List<StructP> structs, List<TopRefableP> topRefables) {
    this.structs = nListWithNonUniqueNames(ImmutableList.copyOf(structs));
    this.topRefables = ImmutableList.copyOf(topRefables);
  }

  public ImmutableList<TopRefableP> topRefables() {
    return topRefables;
  }

  public NList<StructP> structs() {
    return structs;
  }
}

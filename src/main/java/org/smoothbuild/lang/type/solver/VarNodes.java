package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.util.collect.Sets.map;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.HashMap;

import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.solver.ConstrGraphS.Builder;
import org.smoothbuild.util.type.Bounds;

public class VarNodes {
  private final HashMap<VarS, VarNode> nodes;
  private final Bounds<MonoTS> initialBounds;

  public VarNodes(TypeSF typeF) {
    this.initialBounds = new Bounds<>(typeF.nothing(), typeF.any());
    this.nodes = new HashMap<>();
  }

  public VarNode get(VarS var) {
    return nodes.computeIfAbsent(var, v -> new VarNode(v, initialBounds));
  }

  public ConstrGraphS graph() {
    Builder builder = ConstrGraphS.builder();
    for (var varNode : nodes.values()) {
      var uppers = map(varNode.edges(UPPER), VarNode::var);
      builder.addVar(varNode.var(), uppers, varNode.bounds());
    }
    return builder.build();
  }
}

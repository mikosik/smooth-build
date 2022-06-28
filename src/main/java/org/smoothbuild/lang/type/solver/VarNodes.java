package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.Side.UPPER;
import static org.smoothbuild.util.collect.Sets.map;

import java.util.HashMap;

import org.smoothbuild.lang.type.Bounds;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.lang.type.solver.ConstrGraph.Builder;

public class VarNodes {
  private final HashMap<VarS, VarNode> nodes;
  private final Bounds<MonoTS> initialBounds;

  public VarNodes() {
    this.initialBounds = new Bounds<>(TypeFS.nothing(), TypeFS.any());
    this.nodes = new HashMap<>();
  }

  public VarNode get(VarS var) {
    return nodes.computeIfAbsent(var, v -> new VarNode(v, initialBounds));
  }

  public ConstrGraph graph() {
    Builder builder = ConstrGraph.builder();
    for (var varNode : nodes.values()) {
      var uppers = map(varNode.edges(UPPER), VarNode::var);
      builder.addVar(varNode.var(), uppers, varNode.bounds());
    }
    return builder.build();
  }
}

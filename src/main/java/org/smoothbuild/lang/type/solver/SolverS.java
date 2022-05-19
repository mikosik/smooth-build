package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.ConstrS.constrS;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.LinkedList;
import java.util.Queue;

import org.smoothbuild.lang.type.ConstrS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.type.Side;

public class SolverS {
  private final NormalizerS normalizer;
  private final ConstrDecomposer constrDecomposer;
  private final VarNodes varNodes;

  public SolverS(TypeSF typeF) {
    this.normalizer = new NormalizerS(typeF);
    this.constrDecomposer = new ConstrDecomposer();
    this.varNodes = new VarNodes(typeF);
  }

  public void addConstr(ConstrS constr) throws ConstrDecomposeExc {
    Queue<ConstrS> queue = new LinkedList<>();
    var elementaryConstrs = constrDecomposer.decompose(constr);
    for (var elementaryConstr : elementaryConstrs) {
      queue.addAll(normalizer.normalize(elementaryConstr));
    }
    drainQueue(queue);
  }

  private void drainQueue(Queue<ConstrS> queue) throws ConstrDecomposeExc {
    while (!queue.isEmpty()) {
      var constr = queue.remove();
      var lower = constr.lower();
      var upper = constr.upper();
      // TODO or handle methods are corner cases of some more general algorithm
      // they can be replaced by one method capable of handling or cases
      if (lower instanceof VarS lowerVar) {
        if (upper instanceof VarS upperVar) {
          handleVarLowerThanVar(queue, varNodes.get(lowerVar), varNodes.get(upperVar));
        } else {
          handleVarLowerThanNonVar(queue, varNodes.get(lowerVar), upper);
        }
      } else if (upper instanceof VarS upperVar) {
        handleNonVarLowerThanVar(queue, lower, varNodes.get(upperVar));
      } else {
        throw new RuntimeException("Shouldn't happen, constr = " + constr);
      }
    }
  }

  private void handleVarLowerThanVar(Queue<ConstrS> queue, VarNode lowerNode, VarNode upperNode)
      throws ConstrDecomposeExc {
    // 1
    if (!edgeExists(lowerNode, upperNode)) {
      // 1.A
      addEdgesTransitively(lowerNode, upperNode);
      // 1.B
      updateBoundTransitively(lowerNode, upperNode, LOWER);
      // 1.C
      updateBoundTransitively(upperNode, lowerNode, UPPER);
      // 1.D
      addDecomposedToQueue(queue, lowerNode.bound(LOWER), upperNode.bound(UPPER));
    }
  }

  private void addEdgesTransitively(VarNode lowerNode, VarNode upperNode) {
    if (!edgeExists(lowerNode, upperNode)) {
      addUpperEdgesTransitively(lowerNode, upperNode);
      lowerNode.edges(LOWER).forEach(l -> addEdgesTransitively(l, upperNode));
    }
  }

  private void addUpperEdgesTransitively(VarNode lowerNode, VarNode upperNode) {
    if (!edgeExists(lowerNode, upperNode)) {
      addBidirectionalEdge(lowerNode, upperNode);
      upperNode.edges(UPPER).forEach(u -> addUpperEdgesTransitively(lowerNode, u));
    }
  }

  private boolean edgeExists(VarNode lowerNode, VarNode upperNode) {
    return lowerNode.equals(upperNode)
        || lowerNode.edges(UPPER).contains(upperNode);
  }

  private void addBidirectionalEdge(VarNode lowerNode, VarNode upperNode) {
    lowerNode.edges(UPPER).add(upperNode);
    upperNode.edges(LOWER).add(lowerNode);
  }

  private void handleVarLowerThanNonVar(Queue<ConstrS> queue, VarNode lower, TypeS upper)
      throws ConstrDecomposeExc {
    updateBoundTransitively(upper, lower, UPPER);
    addDecomposedToQueue(queue, lower.bound(LOWER), upper);
  }

  private void handleNonVarLowerThanVar(Queue<ConstrS> queue, TypeS lower, VarNode upper)
      throws ConstrDecomposeExc {
    updateBoundTransitively(lower, upper, LOWER);
    addDecomposedToQueue(queue, lower, upper.bound(UPPER));
  }

  private void updateBoundTransitively(VarNode source, VarNode target, Side boundSide) {
    updateBoundTransitively(source.bound(boundSide), target, boundSide);
  }

  private void updateBoundTransitively(TypeS bound, VarNode target, Side boundSide) {
    if (target.mergeBound(boundSide, bound)) {
      target.edges(boundSide.other())
          .forEach(n -> updateBoundTransitively(bound, n, boundSide));
    }
  }

  private void addDecomposedToQueue(Queue<ConstrS> queue, TypeS lower, TypeS upper)
      throws ConstrDecomposeExc {
    queue.addAll(constrDecomposer.decompose(constrS(lower, upper)));
  }

  public ConstrGraphS graph() {
    return varNodes.graph();
  }
}

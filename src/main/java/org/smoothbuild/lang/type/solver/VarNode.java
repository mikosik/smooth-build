package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.MergingTS.merge;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

public class VarNode {
  private final VarS var;
  private Bounds<TypeS> bounds;
  private final Bounds<Set<VarNode>> edges;

  public VarNode(VarS var, Bounds<TypeS> bounds) {
    this.var = var;
    this.bounds = bounds;
    this.edges = new Bounds<>(new HashSet<>(), new HashSet<>());
  }

  public VarS var() {
    return var;
  }

  public Set<VarNode> edges(Side side) {
    return edges.get(side);
  }

  public TypeS bound(Side side) {
    return bounds.get(side);
  }

  public Bounds<TypeS> bounds() {
    return bounds;
  }

  public boolean mergeBound(Side side, TypeS merging) {
    TypeS old = bounds.get(side);
    TypeS merged = merge(old, merging, side.other());
    if (!old.equals(merged)) {
      bounds = bounds.with(side, merged);
      return true;
    }
    return false;
  }
}

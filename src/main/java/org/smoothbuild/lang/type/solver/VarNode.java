package org.smoothbuild.lang.type.solver;

import static org.smoothbuild.lang.type.MergeTS.mergeReduced;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.HashSet;
import java.util.Set;

import org.smoothbuild.lang.type.Bounds;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.Side;
import org.smoothbuild.lang.type.VarS;

public class VarNode {
  private final VarS var;
  private Bounds<MonoTS> bounds;
  private final Bounds<Set<VarNode>> edges;

  public VarNode(VarS var, Bounds<MonoTS> bounds) {
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

  public MonoTS bound(Side side) {
    return bounds.get(side);
  }

  public Bounds<MonoTS> bounds() {
    return bounds;
  }

  public boolean mergeBound(Side side, MonoTS merging) {
    MonoTS old = bounds.get(side);
    MonoTS merged = mergeReduced(set(old, merging), side.other());
    if (!old.equals(merged)) {
      bounds = bounds.with(side, merged);
      return true;
    }
    return false;
  }
}

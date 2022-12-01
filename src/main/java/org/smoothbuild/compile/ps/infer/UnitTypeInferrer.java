package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.util.bindings.Bindings;

/**
 * Infers unit type (which is empty tuple) for each quantified var
 * in any call to polymorphic function when that quantified var:
 * - is not used in function result type
 * - is resolved as temp-var after inferring phase (= it is not constrained).
 */
public class UnitTypeInferrer {
  private final Unifier unifier;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;

  public UnitTypeInferrer(Unifier unifier,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    this.unifier = unifier;
    this.bindings = bindings;
  }

  public void infer(ExprP expr) {
    // @formatter:off
    switch (expr) {
      case CallP       call       -> inferCall(call);
      case NamedArgP   namedArg   -> inferNamedArg(namedArg);
      case OrderP      order      -> inferOrder(order);
      case RefP        ref        -> inferMonoizable(ref);
      case SelectP     select     -> inferSelect(select);
      case StringP     string     -> {}
      case IntP        int_       -> {}
      case BlobP       blob       -> {}
    }
    // @formatter:on
  }

  private void inferCall(CallP call) {
    infer(call.callee());
    call.args().forEach(this::infer);
  }

  private void inferNamedArg(NamedArgP namedArg) {
    infer(namedArg.expr());
  }

  private void inferOrder(OrderP order) {
    order.elems().forEach(this::infer);
  }

  private void inferMonoizable(MonoizableP monoizableP) {
    for (var monoizedVar : monoizableP.monoizeVarMap().values()) {
      var resolvedMonoizedVar = unifier.resolve(monoizedVar);
      for (var var : resolvedMonoizedVar.vars()) {
        if (var.isTemporary()) {
          unifier.unifyOrFailWithRuntimeException(var, new TupleTS(list()));
        }
      }
    }
  }

  private void inferSelect(SelectP select) {
    infer(select.selectable());
  }
}

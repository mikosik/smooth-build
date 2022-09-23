package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.Maps.filterKeys;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.ValP;

import com.google.common.base.Predicate;

/**
 * Handles inferring default type (which is empty tuple) for each quantified var
 * in any call to polymorphic function when that quantified var:
 * - is not used in function result type
 * - is resolved as prefixed after inferring phase (= it is not constrained, nor present
 *   in enclosing function parameters).
 */
public class DefaultTypeInferrer {
  private final Unifier unifier;

  public DefaultTypeInferrer(Unifier unifier) {
    this.unifier = unifier;
  }

  public void infer(ExprP expr) {
    switch (expr) {
      case ValP val -> {}
      case CallP call -> inferCall(call);
      case DefaultArgP defaultArg -> {}
      case NamedArgP namedArg -> inferNamedArg(namedArg);
      case OrderP order -> inferOrder(order);
      case RefP ref -> inferRef(ref);
      case SelectP select -> inferSelect(select);
    }
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

  private void inferRef(RefP ref) {
    var typelike = ref.typelike();
    if (typelike instanceof SchemaS schemaS && schemaS.type() instanceof FuncTS funcTS) {
      Predicate<VarS> presentOnlyInParam = v -> !funcTS.res().vars().contains(v);
      var paramOnlyVars = filterKeys(ref.monoizationMapping(), presentOnlyInParam);
      for (var type : paramOnlyVars.values()) {
        var resolved = unifier.resolve(type);
        resolved.vars().stream()
            .filter(VarS::isTemporary)
            .forEach(v -> unifier.unifySafe(v, new TupleTS(list())));
      }
    }
  }

  private void inferSelect(SelectP select) {
    infer(select.selectable());
  }
}

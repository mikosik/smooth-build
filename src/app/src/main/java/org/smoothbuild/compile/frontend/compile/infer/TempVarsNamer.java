package org.smoothbuild.compile.frontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maps.toMap;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compile.frontend.compile.ast.define.BlobP;
import org.smoothbuild.compile.frontend.compile.ast.define.CallP;
import org.smoothbuild.compile.frontend.compile.ast.define.EvaluableP;
import org.smoothbuild.compile.frontend.compile.ast.define.ExprP;
import org.smoothbuild.compile.frontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compile.frontend.compile.ast.define.IntP;
import org.smoothbuild.compile.frontend.compile.ast.define.LambdaP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compile.frontend.compile.ast.define.OrderP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.SelectP;
import org.smoothbuild.compile.frontend.compile.ast.define.StringP;
import org.smoothbuild.compile.frontend.lang.type.TypeS;
import org.smoothbuild.compile.frontend.lang.type.VarS;
import org.smoothbuild.compile.frontend.lang.type.VarSetS;
import org.smoothbuild.compile.frontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compile.frontend.lang.type.tool.Unifier;
import org.smoothbuild.compile.frontend.lang.type.tool.UnusedVarsGenerator;

public class TempVarsNamer {
  private final Unifier unifier;
  private final VarSetS outerScopeVars;

  public TempVarsNamer(Unifier unifier) {
    this(unifier, varSetS());
  }

  private TempVarsNamer(Unifier unifier, VarSetS outerScopeVars) {
    this.unifier = unifier;
    this.outerScopeVars = outerScopeVars;
  }

  public void nameVarsInNamedValue(NamedValueP namedValue) {
    nameVarsInEvaluable(namedValue);
  }

  public void nameVarsInNamedFunc(NamedFuncP namedFunc) {
    nameVarsInEvaluable(namedFunc);
  }

  private void handleExpr(VarSetS varsInScope, ExprP expr) {
    new TempVarsNamer(unifier, varsInScope).handleExpr(expr);
  }

  private void handleExpr(ExprP expr) {
    // @formatter:off
    switch (expr) {
      case CallP callP -> handleCall(callP);
      case InstantiateP instantiateP -> handleInstantiate(instantiateP);
      case NamedArgP namedArgP -> handleExpr(namedArgP.expr());
      case OrderP orderP -> handleOrder(orderP);
      case SelectP selectP -> handleExpr(selectP.selectable());
      case IntP intP -> {}
      case BlobP blobP -> {}
      case StringP stringP -> {}
    }
    // @formatter:on
  }

  private void handleCall(CallP call) {
    handleChildren(list(call.callee()).appendAll(call.args()));
  }

  private void handleInstantiate(InstantiateP instantiateP) {
    switch (instantiateP.polymorphic()) {
      case LambdaP lambdaP -> handleLambda(lambdaP);
      case ReferenceP referenceP -> {}
    }
  }

  private void handleLambda(LambdaP lambdaP) {
    nameVarsInEvaluable(lambdaP);
  }

  private void handleOrder(OrderP order) {
    handleChildren(order.elems());
  }

  private void handleChildren(List<ExprP> children) {
    for (var child : children) {
      handleExpr(child);
    }
  }

  private void nameVarsInEvaluable(EvaluableP evaluable) {
    var resolvedT = unifier.resolve(evaluable.typeS());
    var body = evaluable.body();
    var thisScopeVars = resolvedT.vars().filter(v -> !v.isTemporary());
    var varsInScope = outerScopeVars.withAdded(thisScopeVars);
    body.ifPresent(b -> handleExpr(varsInScope, b));
    var resolvedAndRenamedT = nameVars(resolvedT, thisScopeVars);
    unifier.addOrFailWithRuntimeException(new EqualityConstraint(resolvedAndRenamedT, resolvedT));
  }

  private static TypeS nameVars(TypeS resolvedT, VarSetS reservedVars) {
    var vars = resolvedT.vars();
    var varsToRename = vars.filter(VarS::isTemporary);
    var varGenerator = new UnusedVarsGenerator(reservedVars);
    var mapping = toMap(varsToRename, v -> (TypeS) varGenerator.next());
    return resolvedT.mapVars(mapping);
  }
}

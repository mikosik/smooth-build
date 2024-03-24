package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.EvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVar;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnusedVarsGenerator;

public class TempVarsNamer {
  private final Unifier unifier;
  private final SVarSet outerScopeVars;

  public TempVarsNamer(Unifier unifier) {
    this(unifier, varSetS());
  }

  private TempVarsNamer(Unifier unifier, SVarSet outerScopeVars) {
    this.unifier = unifier;
    this.outerScopeVars = outerScopeVars;
  }

  public void nameVarsInNamedValue(NamedValueP namedValue) {
    nameVarsInEvaluable(namedValue);
  }

  public void nameVarsInNamedFunc(NamedFuncP namedFunc) {
    nameVarsInEvaluable(namedFunc);
  }

  private void handleExpr(SVarSet varsInScope, ExprP expr) {
    new TempVarsNamer(unifier, varsInScope).handleExpr(expr);
  }

  private void handleExpr(ExprP expr) {
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
    handleChildren(order.elements());
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
    var varsInScope = outerScopeVars.withAddedAll(thisScopeVars);
    body.ifPresent(b -> handleExpr(varsInScope, b));
    var resolvedAndRenamedT = nameVars(resolvedT, thisScopeVars);
    unifier.addOrFailWithRuntimeException(new EqualityConstraint(resolvedAndRenamedT, resolvedT));
  }

  private static SType nameVars(SType resolvedT, SVarSet reservedVars) {
    var vars = resolvedT.vars();
    var varsToRename = vars.filter(SVar::isTemporary);
    var varGenerator = new UnusedVarsGenerator(reservedVars);
    var mapping = varsToRename.toList().toMap(v -> (SType) varGenerator.next());
    return resolvedT.mapVars(mapping);
  }
}

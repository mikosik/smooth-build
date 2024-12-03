package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
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

  public void nameVarsInNamedValue(PNamedValue namedValue) {
    nameVarsInEvaluable(namedValue);
  }

  public void nameVarsInNamedFunc(PNamedFunc namedFunc) {
    nameVarsInEvaluable(namedFunc);
  }

  private void handleExpr(SVarSet varsInScope, PExpr expr) {
    new TempVarsNamer(unifier, varsInScope).handleExpr(expr);
  }

  private void handleExpr(PExpr expr) {
    switch (expr) {
      case PCall pCall -> handleCall(pCall);
      case PInstantiate pInstantiate -> handleInstantiate(pInstantiate);
      case PNamedArg pNamedArg -> handleExpr(pNamedArg.expr());
      case POrder pOrder -> handleOrder(pOrder);
      case PSelect pSelect -> handleExpr(pSelect.selectable());
      case PInt pInt -> {}
      case PBlob pBlob -> {}
      case PString pString -> {}
    }
  }

  private void handleCall(PCall call) {
    handleChildren(list(call.callee()).appendAll(call.args()));
  }

  private void handleInstantiate(PInstantiate pInstantiate) {
    switch (pInstantiate.polymorphic()) {
      case PLambda pLambda -> handleLambda(pLambda);
      case PReference pReference -> {}
    }
  }

  private void handleLambda(PLambda pLambda) {
    nameVarsInEvaluable(pLambda);
  }

  private void handleOrder(POrder order) {
    handleChildren(order.elements());
  }

  private void handleChildren(List<PExpr> children) {
    for (var child : children) {
      handleExpr(child);
    }
  }

  private void nameVarsInEvaluable(PEvaluable evaluable) {
    var resolvedT = unifier.resolve(evaluable.sType());
    var body = evaluable.body();
    var localScopeVars = resolvedT.vars().filter(v -> !v.isTemporary());
    var varsInScope = outerScopeVars.withAddedAll(localScopeVars);
    body.ifPresent(b -> handleExpr(varsInScope, b));
    var resolvedAndRenamedT = nameVars(resolvedT, localScopeVars);
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

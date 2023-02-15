package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;

import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;
import org.smoothbuild.compile.fs.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.lang.type.tool.UnusedVarsGenerator;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.EvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.InstantiateP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;

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
    new TempVarsNamer(unifier, varsInScope)
        .handleExpr(expr);
  }

  private void handleExpr(ExprP expr) {
    // @formatter:off
    switch (expr) {
      case CallP          callP          -> handleCall(callP);
      case InstantiateP   instantiateP   -> handleInstantiate(instantiateP);
      case NamedArgP      namedArgP      -> handleExpr(namedArgP.expr());
      case OrderP         orderP         -> handleOrder(orderP);
      case SelectP        selectP        -> handleExpr(selectP.selectable());
      case IntP           intP           -> {}
      case BlobP          blobP          -> {}
      case StringP        stringP        -> {}
    }
    // @formatter:on
  }

  private void handleCall(CallP call) {
    handleChildren(concat(call.callee(), call.args()));
  }

  private void handleInstantiate(InstantiateP instantiateP) {
    switch (instantiateP.polymorphic()) {
      case AnonymousFuncP anonymousFuncP -> handleAnonymousFunc(anonymousFuncP);
      case ReferenceP referenceP -> {}
    }
  }

  private void handleAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    nameVarsInEvaluable(anonymousFuncP);
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

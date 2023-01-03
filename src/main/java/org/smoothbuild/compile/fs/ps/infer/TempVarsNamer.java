package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.lang.type.tool.UnusedVarsGenerator;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.EvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.RefP;
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

  private VarSetS nameVarsInEvaluable(EvaluableP evaluable) {
    var resolvedT = unifier.resolve(evaluable.typeS());
    return nameVars(resolvedT, evaluable.body());
  }

  private VarSetS handleExpr(VarSetS varsInScope, ExprP expr) {
    return new TempVarsNamer(unifier, varsInScope)
        .handleExpr(expr);
  }

  private VarSetS handleExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP          callP          -> handleCall(callP);
      case AnonymousFuncP anonymousFuncP -> handleAnonymousFunc(anonymousFuncP);
      case NamedArgP      namedArgP      -> handleExpr(namedArgP.expr());
      case OrderP         orderP         -> handleOrder(orderP);
      case RefP           refP           -> varSetS();
      case SelectP        selectP        -> handleExpr(selectP.selectable());
      case IntP           intP           -> varSetS();
      case BlobP          blobP          -> varSetS();
      case StringP        stringP        -> varSetS();
    };
    // @formatter:on
  }

  private VarSetS handleCall(CallP call) {
    return handleChildren(concat(call.callee(), call.args()));
  }

  private VarSetS handleAnonymousFunc(AnonymousFuncP anonymousFuncP) {
    return nameVarsInEvaluable(anonymousFuncP);
  }

  private VarSetS handleOrder(OrderP order) {
    return handleChildren(order.elems());
  }

  private VarSetS handleChildren(List<ExprP> children) {
    var vars = new HashSet<VarS>();
    for (var child : children) {
      vars.addAll(handleExpr(child));
    }
    return varSetS(vars);
  }

  private VarSetS nameVars(TypeS resolvedT, Optional<ExprP> body) {
    var thisScopeVars = resolvedT.vars().filter(v -> !v.isTemporary());
    var varsInScope = outerScopeVars.withAdded(thisScopeVars);
    var innerScopeVars = body.map(b -> handleExpr(varsInScope, b)).orElse(varSetS());
    var reservedVars = varsInScope.withAdded(innerScopeVars);
    var resolvedAndRenamedT = nameVars(resolvedT, reservedVars);
    unifier.unifyOrFailWithRuntimeException(resolvedAndRenamedT, resolvedT);
    return resolvedAndRenamedT.vars().withAdded(innerScopeVars);
  }

  private static TypeS nameVars(TypeS resolvedT, VarSetS reservedVars) {
    var vars = resolvedT.vars();
    var varsToRename = vars.filter(VarS::isTemporary);
    var varGenerator = new UnusedVarsGenerator(reservedVars);
    var mapping = toMap(varsToRename, v -> varGenerator.next());
    return resolvedT.mapVars(mapping);
  }
}

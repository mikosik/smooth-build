package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Maps.mapKeys;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Optional;

import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.AnonFuncP;
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
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.out.log.Logger;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;

  public TypeInferrerResolve(Unifier unifier, Logger logger) {
    this.unifier = unifier;
    this.logger = logger;
  }

  public boolean resolveNamedValue(NamedValueP value) {
    var resolvedEvalT = unifier.resolve(value.typeS());
    if (resolveBody(value.body())) {
      // Smooth language does not allow nesting named value yet so all vars are quantified.
      var quantifiedVars = resolvedEvalT.vars();
      value.setSchemaS(new SchemaS(quantifiedVars, resolvedEvalT));
      return true;
    } else {
      return false;
    }
  }

  public boolean resolveNamedFunc(NamedFuncP namedFuncP) {
    if (resolveBody(namedFuncP.body())) {
      var resolvedFuncT = (FuncTS) unifier.resolve(namedFuncP.typeS());
      // Smooth language does not allow nesting functions yet so all vars are quantified.
      var quantifiedVars = resolvedFuncT.vars();
      namedFuncP.setSchemaS(new FuncSchemaS(quantifiedVars, resolvedFuncT));
      return true;
    } else {
      return false;
    }
  }

  private boolean resolveBody(Optional<ExprP> body) {
    return body.map(this::resolveBody).orElse(true);
  }

  private boolean resolveBody(ExprP body) {
    inferUnitTypes(body);
    return resolveExpr(body);
  }

  private void inferUnitTypes(ExprP expr) {
    new UnitTypeInferrer(unifier).infer(expr);
  }

  private boolean resolveExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP       callP       -> resolveCall(callP);
      case AnonFuncP   anonFuncP   -> resolveAnonFunc(anonFuncP);
      case NamedArgP   namedArgP   -> resolveNamedArg(namedArgP);
      case OrderP      orderP      -> resolveOrder(orderP);
      case SelectP     selectP     -> resolveSelect(selectP);
      case RefP        refP        -> resolveMonoizable(refP);
      case StringP     stringP     -> resolveExprType(stringP);
      case IntP        intP        -> resolveExprType(intP);
      case BlobP       blobP       -> resolveExprType(blobP);
    };
    // @formatter:on
  }

  private boolean resolveCall(CallP callP) {
    return resolveExpr(callP.callee())
        && callP.positionedArgs().get().stream().allMatch(this::resolveExpr)
        && resolveExprType(callP);
  }

  private boolean resolveAnonFunc(AnonFuncP anonFuncP) {
    if (resolveBody(anonFuncP.body())) {
      // `(VarS)` cast is safe because anonFuncP.monoizeVarMap().keys() has only
      // TempVarS/VarS.
      var varMapWithResolvedKeys =
          mapKeys(anonFuncP.monoizeVarMap(), type -> (VarS) unifier.resolve(type));
      anonFuncP.setMonoizeVarMap(varMapWithResolvedKeys);
      if (resolveMonoizable(anonFuncP)) {
        var schemaS = anonFuncP.schemaS();
        var resolvedT = (FuncTS) unifier.resolve(schemaS.type());
        var quantifiedVars = varSetS(
            schemaS.quantifiedVars().stream().map(v -> (VarS) unifier.resolve(v)).toList());
        anonFuncP.setSchemaS(new FuncSchemaS(quantifiedVars, resolvedT));
        return true;
      }
    }
    return false;
  }

  private boolean resolveNamedArg(NamedArgP namedArgP) {
    return resolveExpr(namedArgP.expr())
        && resolveExprType(namedArgP);
  }

  private boolean resolveOrder(OrderP orderP) {
    return orderP.elems().stream().allMatch(this::resolveExpr)
        && resolveExprType(orderP);
  }

  private boolean resolveSelect(SelectP selectP) {
    return resolveExpr(selectP.selectable())
        && resolveExprType(selectP);
  }

  private boolean resolveExprType(ExprP exprP) {
    exprP.setTypeS(unifier.resolve(exprP.typeS()));
    return true;
  }

  private boolean resolveMonoizable(MonoizableP monoizableP) {
    var varMapWithResolvedValues = mapValues(monoizableP.monoizeVarMap(), unifier::resolve);
    if (varMapWithResolvedValues.values().stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(monoizableP.loc(), "Cannot infer actual type parameters."));
      return false;
    }
    monoizableP.setMonoizeVarMap(varMapWithResolvedValues);
    return true;
  }

  private boolean hasTempVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::isTemporary);
  }
}

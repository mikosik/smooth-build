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
import org.smoothbuild.compile.lang.type.VarSetS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.AnonFuncP;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.EvaluableP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.out.log.Logger;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;

  public TypeInferrerResolve(Unifier unifier, Logger logger) {
    this.unifier = unifier;
    this.logger = logger;
  }

  public boolean resolveNamedValue(NamedValueP namedValueP) {
    return resolveEvaluable(namedValueP);
  }

  public boolean resolveNamedFunc(NamedFuncP namedFuncP) {
    return resolveEvaluable(namedFuncP);
  }

  private boolean resolveEvaluable(EvaluableP evaluableP) {
    return resolveBody(evaluableP.body()) && resolveSchema(evaluableP);
  }

  private boolean resolveSchema(EvaluableP evaluableP) {
    // @formatter:off
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(resolveSchema(valueP.schemaS()));
      case FuncP       funcP  -> funcP.setSchemaS(resolveSchema(funcP.schemaS()));
      // TODO remove once bug in intellij is fixed
      default -> throw new IllegalStateException("Unexpected value: " + evaluableP);
    }
    // @formatter:off
    return true;
  }

  private FuncSchemaS resolveSchema(FuncSchemaS funcSchemaS) {
    return new FuncSchemaS(resolveQuantifiedVars(funcSchemaS), (FuncTS) resolveType(funcSchemaS));
  }

  private SchemaS resolveSchema(SchemaS schemaS) {
    return new SchemaS(resolveQuantifiedVars(schemaS), resolveType(schemaS));
  }

  private VarSetS resolveQuantifiedVars(SchemaS schemaS) {
    return varSetS(schemaS.quantifiedVars().stream().map(v -> (VarS) unifier.resolve(v)).toList());
  }

  private TypeS resolveType(SchemaS schemaS) {
    return unifier.resolve(schemaS.type());
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
    return resolveEvaluable(anonFuncP) && resolveMonoizableAnonFunc(anonFuncP);
  }

  private boolean resolveMonoizableAnonFunc(AnonFuncP anonFuncP) {
    // `(VarS)` cast is safe because anonFuncP.monoizeVarMap().keys() has only
    // TempVarS/VarS.
    var varMapWithResolvedKeys = mapKeys(anonFuncP.monoizeVarMap(), v -> (VarS) unifier.resolve(v));
    anonFuncP.setMonoizeVarMap(varMapWithResolvedKeys);
    return resolveMonoizable(anonFuncP);
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

package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Optional;
import java.util.function.Predicate;

import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.SchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;
import org.smoothbuild.compile.lang.type.tool.Unifier;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.MonoizableP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.NamedValueP;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.bindings.Bindings;

public class TypeInferrerResolve {
  private final Unifier unifier;
  private final Logger logger;
  private final Bindings<? extends Optional<? extends RefableS>> bindings;

  public TypeInferrerResolve(Unifier unifier, Logger logger,
      Bindings<? extends Optional<? extends RefableS>> bindings) {
    this.unifier = unifier;
    this.logger = logger;
    this.bindings = bindings;
  }

  public Optional<SchemaS> resolve(NamedValueP val, TypeS evalT) {
    TypeS resolvedEvalT = unifier.resolve(evalT);
    if (val.type().isPresent()) {
      if (!evalT.equals(resolvedEvalT)) {
        logger.log(compileError(val.loc(), val.q() + " body type is not equal to declared type."));
        return Optional.empty();
      }
    } else {
      resolvedEvalT = renameVarsAndUnify(resolvedEvalT, VarS::isTemporary);
    }
    if (!resolveBody(val.body())) {
      return Optional.empty();
    }
    return Optional.of(new SchemaS(resolvedEvalT));
  }

  public boolean resolveParamDefaultValue(ExprP exprP) {
    var resolvedType = unifier.resolve(exprP.typeS());
    exprP.setTypeS(renameVarsAndUnify(resolvedType, v -> true));
    return resolveBody(exprP);
  }

  public Optional<FuncSchemaS> resolve(FuncP func, FuncTS funcT) {
    var resolvedFuncT = (FuncTS) unifier.resolve(funcT);
    if (func.resT().isPresent()) {
      if (!funcT.res().equals(resolvedFuncT.res())) {
        logger.log(compileError(func.resT().get().loc(),
            func.q() + " body type is not equal to declared type."));
        return Optional.empty();
      }
    } else {
      resolvedFuncT = (FuncTS) renameVarsAndUnify(resolvedFuncT, VarS::isTemporary);
    }
    if (!resolveBody(func.body())) {
      return Optional.empty();
    }
    return Optional.of(new FuncSchemaS(resolvedFuncT));
  }

  private boolean resolveBody(Optional<ExprP> body) {
    return body.map(this::resolveBody).orElse(true);
  }

  private boolean resolveBody(ExprP body) {
    inferUnitTypes(body);
    return resolve(body);
  }

  private void inferUnitTypes(ExprP expr) {
    new UnitTypeInferrer(unifier, bindings).infer(expr);
  }

  private TypeS renameVarsAndUnify(TypeS resolvedT, Predicate<VarS> shouldRename) {
    var resolvedAndRenamedEvalT = resolvedT.renameVars(shouldRename);
    unifier.unifySafe(resolvedAndRenamedEvalT, resolvedT);
    return resolvedAndRenamedEvalT;
  }

  public boolean resolve(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP       callP       -> resolve(callP);
      case NamedArgP   namedArgP   -> resolve(namedArgP);
      case OrderP      orderP      -> resolve(orderP);
      case SelectP     selectP     -> resolve(selectP);
      case RefP        refP        -> resolve(refP);
      case DefaultArgP defaultArgP -> resolve(defaultArgP);
      case StringP     stringP     -> resolveExprType(stringP);
      case IntP        intP        -> resolveExprType(intP);
      case BlobP       blobP       -> resolveExprType(blobP);
    };
    // @formatter:on
  }

  private boolean resolve(CallP callP) {
    return resolve(callP.callee())
        && callP.positionedArgs().get().stream().allMatch(this::resolve)
        && resolveExprType(callP);
  }

  private boolean resolve(NamedArgP namedArgP) {
    return resolve(namedArgP.expr())
        && resolveExprType(namedArgP);
  }

  private boolean resolve(OrderP orderP) {
    return orderP.elems().stream().allMatch(this::resolve)
        && resolveExprType(orderP);
  }

  private boolean resolve(SelectP selectP) {
    return resolve(selectP.selectable())
        && resolveExprType(selectP);
  }

  private boolean resolveExprType(ExprP exprP) {
    exprP.setTypeS(unifier.resolve(exprP.typeS()));
    return true;
  }

  private boolean resolve(MonoizableP monoizableP) {
    var resolvedMonoizeVarMap = mapValues(monoizableP.monoizeVarMap(), unifier::resolve);
    monoizableP.setMonoizeVarMap(resolvedMonoizeVarMap);
    if (resolvedMonoizeVarMap.values().stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(monoizableP.loc(), "Cannot infer actual type parameters."));
      return false;
    }
    return true;
  }

  private boolean hasTempVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::isTemporary);
  }
}

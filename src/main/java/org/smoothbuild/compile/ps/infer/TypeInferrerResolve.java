package org.smoothbuild.compile.ps.infer;

import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.compile.ps.infer.BindingsHelper.funcBodyScopeBindings;
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
import org.smoothbuild.compile.ps.ast.refable.NamedFuncP;
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

  public Optional<SchemaS> resolveNamedValue(NamedValueP value) {
    var resolvedEvalT = unifier.resolve(value.typeS());
    resolvedEvalT = renameVarsAndUnify(resolvedEvalT, VarS::isTemporary);
    if (!resolveBody(value.body())) {
      return Optional.empty();
    }
    return Optional.of(new SchemaS(resolvedEvalT));
  }

  public boolean resolveParamDefaultValue(ExprP exprP) {
    var resolvedType = unifier.resolve(exprP.typeS());
    exprP.setTypeS(renameVarsAndUnify(resolvedType, v -> true));
    return resolveBody(exprP);
  }

  public Optional<FuncSchemaS> resolveNamedFunc(NamedFuncP namedFunc) {
    var bodyBindings = funcBodyScopeBindings(bindings, namedFunc.params());
    return new TypeInferrerResolve(unifier, logger, bodyBindings)
        .resolveNamedFuncImpl(namedFunc);
  }

  private Optional<FuncSchemaS> resolveNamedFuncImpl(NamedFuncP namedFunc) {
    var resolvedFuncT = (FuncTS) unifier.resolve(namedFunc.typeS());
    resolvedFuncT = (FuncTS) renameVarsAndUnify(resolvedFuncT, VarS::isTemporary);
    if (resolveBody(namedFunc.body())) {
      return Optional.of(new FuncSchemaS(resolvedFuncT));
    } else {
      return Optional.empty();
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
    new UnitTypeInferrer(unifier, bindings).infer(expr);
  }

  private TypeS renameVarsAndUnify(TypeS resolvedT, Predicate<VarS> shouldRename) {
    var resolvedAndRenamedEvalT = resolvedT.renameVars(shouldRename);
    unifier.unifySafe(resolvedAndRenamedEvalT, resolvedT);
    return resolvedAndRenamedEvalT;
  }

  private boolean resolveExpr(ExprP expr) {
    // @formatter:off
    return switch (expr) {
      case CallP       callP       -> resolveCall(callP);
      case NamedArgP   namedArgP   -> resolveNamedArg(namedArgP);
      case OrderP      orderP      -> resolveOrder(orderP);
      case SelectP     selectP     -> resolveSelect(selectP);
      case RefP        refP        -> resolveMonoizable(refP);
      case DefaultArgP defaultArgP -> resolveMonoizable(defaultArgP);
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
    var resolvedMonoizeVarMap = mapValues(monoizableP.monoizeVarMap(), unifier::resolve);
    if (resolvedMonoizeVarMap.values().stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(monoizableP.loc(), "Cannot infer actual type parameters."));
      return false;
    }
    monoizableP.setMonoizeVarMap(resolvedMonoizeVarMap);
    return true;
  }

  private boolean hasTempVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::isTemporary);
  }
}

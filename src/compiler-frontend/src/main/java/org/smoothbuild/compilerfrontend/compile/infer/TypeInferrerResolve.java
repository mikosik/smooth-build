package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.VarSetS.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.EvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.PolymorphicP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;
import org.smoothbuild.compilerfrontend.lang.type.FuncTS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarS;
import org.smoothbuild.compilerfrontend.lang.type.VarSetS;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;

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
    switch (evaluableP) {
      case NamedValueP valueP -> valueP.setSchemaS(resolveSchema(valueP.schemaS()));
      case FuncP funcP -> funcP.setSchemaS(resolveSchema(funcP.schemaS()));
    }
    return true;
  }

  private FuncSchemaS resolveSchema(FuncSchemaS funcSchemaS) {
    return new FuncSchemaS(resolveQuantifiedVars(funcSchemaS), (FuncTS) resolveType(funcSchemaS));
  }

  private SchemaS resolveSchema(SchemaS schemaS) {
    return new SchemaS(resolveQuantifiedVars(schemaS), resolveType(schemaS));
  }

  private VarSetS resolveQuantifiedVars(SchemaS schemaS) {
    return varSetS(
        schemaS.quantifiedVars().stream().map(v -> (VarS) unifier.resolve(v)).toList());
  }

  private TypeS resolveType(SchemaS schemaS) {
    return unifier.resolve(schemaS.type());
  }

  private boolean resolveBody(Maybe<ExprP> body) {
    return body.map(this::resolveBody).getOr(true);
  }

  private boolean resolveBody(ExprP body) {
    inferUnitTypes(body);
    return resolveExpr(body);
  }

  private void inferUnitTypes(ExprP expr) {
    new UnitTypeInferrer(unifier).infer(expr);
  }

  private boolean resolveExpr(ExprP expr) {
    return switch (expr) {
      case CallP callP -> resolveCall(callP);
      case InstantiateP instantiateP -> resolveInstantiate(instantiateP);
      case NamedArgP namedArgP -> resolveNamedArg(namedArgP);
      case OrderP orderP -> resolveOrder(orderP);
      case SelectP selectP -> resolveSelect(selectP);
      case StringP stringP -> resolveExprType(stringP);
      case IntP intP -> resolveExprType(intP);
      case BlobP blobP -> resolveExprType(blobP);
    };
  }

  private boolean resolveCall(CallP callP) {
    return resolveExpr(callP.callee())
        && callP.positionedArgs().stream().allMatch(this::resolveExpr)
        && resolveExprType(callP);
  }

  private boolean resolveInstantiate(InstantiateP instantiateP) {
    return resolvePolymorphic(instantiateP.polymorphic())
        && resolveInstantiateTypeArgs(instantiateP);
  }

  private boolean resolveInstantiateTypeArgs(InstantiateP instantiateP) {
    var resolvedTypeArgs = instantiateP.typeArgs().map(unifier::resolve);
    if (resolvedTypeArgs.stream().anyMatch(this::hasTempVar)) {
      logger.log(compileError(instantiateP.location(), "Cannot infer actual type parameters."));
      return false;
    }
    instantiateP.setTypeArgs(resolvedTypeArgs);
    return true;
  }

  private boolean resolvePolymorphic(PolymorphicP polymorphicP) {
    return switch (polymorphicP) {
      case LambdaP lambdaP -> resolveLambda(lambdaP);
      case ReferenceP referenceP -> true;
    };
  }

  private boolean resolveLambda(LambdaP lambdaP) {
    return resolveEvaluable(lambdaP);
  }

  private boolean resolveNamedArg(NamedArgP namedArgP) {
    return resolveExpr(namedArgP.expr()) && resolveExprType(namedArgP);
  }

  private boolean resolveOrder(OrderP orderP) {
    return orderP.elements().stream().allMatch(this::resolveExpr) && resolveExprType(orderP);
  }

  private boolean resolveSelect(SelectP selectP) {
    return resolveExpr(selectP.selectable()) && resolveExprType(selectP);
  }

  private boolean resolveExprType(ExprP exprP) {
    exprP.setTypeS(unifier.resolve(exprP.typeS()));
    return true;
  }

  private boolean hasTempVar(TypeS t) {
    return t.vars().stream().anyMatch(VarS::isTemporary);
  }
}

package org.smoothbuild.compilerfrontend.compile.infer;

import static java.util.Comparator.comparing;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyEvaluable;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeResolver.resolveNamedEvaluable;
import static org.smoothbuild.compilerfrontend.compile.infer.UnitTypeInferrer.inferUnitTypes;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.STypeVar.typeParamsToSourceCode;

import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitTypeParams;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PPolyEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeScheme;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
import org.smoothbuild.compilerfrontend.lang.type.tool.AlphabeticalTypeNameGenerator;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class InferTypes implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    try {
      new Worker().visit(pModule);
    } catch (TypeException e) {
      return newOutput(pModule, list(e.log()));
    }
    return newOutput(pModule, list());
  }

  private static Output<PModule> newOutput(PModule pModule, List<Log> logs) {
    return output(pModule, COMPILER_FRONT_LABEL.append(":inferTypes"), logs);
  }

  public static class Worker extends PModuleVisitor<TypeException> {
    @Override
    public void visitStruct(PStruct pStruct) throws TypeException {
      inferStructType(pStruct);
    }

    private void inferStructType(PStruct struct) throws TypeException {
      NList<SItemSig> sItemSigs = struct.fields().map(this::inferFieldSig);
      var sStructType = new SStructType(struct.fqn(), sItemSigs);
      struct.setSType(sStructType);
    }

    private SItemSig inferFieldSig(PItem field) throws TypeException {
      var type = field.pType();
      if (type instanceof PExplicitType explicit) {
        SType sType = explicit.infer();
        if (sType.typeVars().isEmpty()) {
          type.setSType(sType);
          return new SItemSig(sType, field.name());
        } else {
          var message = "Field type cannot be polymorphic. Found field %s with type %s."
              .formatted(field.q(), sType.q());
          throw new TypeException(compileError(type, message));
        }
      } else {
        throw new RuntimeException("Implicit field types in Struct are forbidden.");
      }
    }

    @Override
    public void visitPolyEvaluable(PPolyEvaluable pPolyEvaluable) throws TypeException {
      var unifier = new Unifier();
      var evaluable = pPolyEvaluable.evaluable();
      unifyEvaluable(unifier, evaluable);
      resolveTypeParams(pPolyEvaluable, unifier);
      inferUnitTypes(unifier, evaluable);
      resolveNamedEvaluable(unifier, evaluable);
      if (evaluable instanceof PNamedFunc pNamedFunc) {
        detectTypeErrorsBetweenParamAndItsDefaultValue(pNamedFunc);
      }
    }

    private void resolveTypeParams(PPolyEvaluable pPolyEvaluable, Unifier unifier)
        throws TypeException {
      var typeVars = unifier.resolve(pPolyEvaluable.evaluable().type()).typeVars();
      switch (pPolyEvaluable.pTypeParams()) {
        case PExplicitTypeParams explicit -> checkExplicitTypeParams(explicit, typeVars);
        case PImplicitTypeParams implicit -> resolveImplicitTypeParams(implicit, typeVars, unifier);
      }
    }

    private void checkExplicitTypeParams(
        PExplicitTypeParams explicitTypeParams, Set<STypeVar> typeVars) throws TypeException {
      var explicit = explicitTypeParams.explicitTypeVars();
      if (!explicit.toSet().equals(typeVars)) {
        // Sort to make error message stable so tests are not flaky.
        var sortedTypeParams = typeVars.toList().sortUsing(comparing(STypeVar::name));
        throw new TypeException(compileError(
            explicitTypeParams.location(),
            "Type parameters are declared as " + explicitTypeParams.q()
                + " but inferred type parameters are " + q(typeParamsToSourceCode(sortedTypeParams))
                + "."));
      }
    }

    private void resolveImplicitTypeParams(
        PImplicitTypeParams implicit, Set<STypeVar> typeVars, Unifier unifier) {
      implicit.setTypeVars(convertFlexibleVarsToRigid(unifier, typeVars.toList()));
    }

    public static List<STypeVar> convertFlexibleVarsToRigid(
        Unifier unifier, List<STypeVar> typeVars) {
      var nameGenerator = new AlphabeticalTypeNameGenerator();
      return typeVars.map(typeVar -> {
        if (typeVar.isFlexibleTypeVar()) {
          var rigidTypeVar = new STypeVar(nameGenerator.next());
          unifier.addOrFailWithRuntimeException(new Constraint(typeVar, rigidTypeVar));
          return rigidTypeVar;
        } else {
          return typeVar;
        }
      });
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(PNamedFunc namedFunc)
        throws TypeException {
      var params = namedFunc.params();
      for (var param : params) {
        param.defaultValue().ifPresent(defaultValue -> {
          var unifier = new Unifier();
          var paramType = param.pType().sType();
          var flexibleParamType = toFlexible(paramType, unifier);
          var defaultValueSchema = defaultValue.referenced().typeScheme();
          var defaultValueFlexibleType = toFlexible(defaultValueSchema, unifier);
          try {
            unifier.add(new Constraint(flexibleParamType, defaultValueFlexibleType));
          } catch (UnifierException e) {
            var defaultValueTypeString = defaultValueSchema.typeParams().isEmpty()
                ? defaultValueSchema.type().q()
                : defaultValueSchema.q();
            var paramTypeString = q(paramType.specifier());
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(param.q(), paramTypeString, defaultValueTypeString);
            throw new TypeException(compileError(param.location(), message), e);
          }
        });
      }
    }

    private SType toFlexible(SType resolvedParamType, Unifier unifier) {
      return toFlexible(resolvedParamType.typeVars(), resolvedParamType, unifier);
    }

    private SType toFlexible(STypeScheme defaultValueSchema, Unifier unifier) {
      return toFlexible(defaultValueSchema.typeParams(), defaultValueSchema.type(), unifier);
    }

    private static SType toFlexible(Collection<STypeVar> typeVars, SType type, Unifier unifier) {
      var mapping = typeVars.toMap(v -> (SType) unifier.newFlexibleTypeVar());
      return type.mapTypeVars(mapping);
    }
  }
}

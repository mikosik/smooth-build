package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyNamedValue;
import static org.smoothbuild.compilerfrontend.compile.infer.FlexibleToRigidVarConverter.convertFlexibleVarsToRigid;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeResolver.resolveFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeResolver.resolveNamedValue;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVar;
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

  public static class Worker extends PScopingModuleVisitor<TypeException> {
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
      var type = scope().translate(field.type());
      if (type.typeVars().isEmpty()) {
        field.type().setSType(type);
        return new SItemSig(type, field.name());
      } else {
        var message = "Field type cannot be polymorphic. Found field %s with type %s."
            .formatted(field.q(), type.q());
        throw new TypeException(compileError(field.type(), message));
      }
    }

    // value

    @Override
    public void visitNamedValue(PNamedValue namedValue) throws TypeException {
      var unifier = new Unifier();
      unifyNamedValue(unifier, scope(), namedValue);
      convertFlexibleVarsToRigid(unifier, namedValue);
      resolveNamedValue(unifier, namedValue);
    }

    // func

    @Override
    public void visitNamedFunc(PNamedFunc namedFunc) throws TypeException {
      var unifier = new Unifier();
      unifyFunc(unifier, scope(), namedFunc);
      convertFlexibleVarsToRigid(unifier, namedFunc);
      resolveFunc(unifier, namedFunc);
      detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(PNamedFunc namedFunc)
        throws TypeException {
      var params = namedFunc.params();
      for (var param : params) {
        param.defaultValueFqn().ifPresent(defaultValueFqn -> {
          var funcSchema = namedFunc.schema();
          var unifier = new Unifier();
          var resolvedParamType = param.type().sType();
          var paramType =
              replaceTypeVarsWithFlexible(funcSchema.typeParams(), resolvedParamType, unifier);
          var sSchema = scope().schemaFor(defaultValueFqn);
          var defaultValueType = replaceTypeParamVarsWithFlexibleTypeVars(sSchema, unifier);
          try {
            unifier.add(new Constraint(paramType, defaultValueType));
          } catch (UnifierException e) {
            var defaultValueTypeString =
                sSchema.typeParams().isEmpty() ? sSchema.type().q() : sSchema.q();
            var paramTypeString = q(resolvedParamType.specifier(funcSchema.typeParams()));
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(param.q(), paramTypeString, defaultValueTypeString);
            throw new TypeException(compileError(param.location(), message), e);
          }
        });
      }
    }

    private static SType replaceTypeParamVarsWithFlexibleTypeVars(
        SSchema sSchema, Unifier unifier) {
      return replaceTypeVarsWithFlexible(sSchema.typeParams(), sSchema.type(), unifier);
    }

    private static SType replaceTypeVarsWithFlexible(
        List<STypeVar> typeVars, SType type, Unifier unifier) {
      var mapping = typeVars.toMap(v -> (SType) unifier.newFlexibleTypeVar());
      return type.mapTypeVars(mapping);
    }
  }
}

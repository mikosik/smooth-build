package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
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
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
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
        field.setSType(type);
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
      if (namedFunc instanceof PConstructor constructor) {
        visitConstructor(constructor);
      } else {
        var unifier = new Unifier();
        unifyFunc(unifier, scope(), namedFunc);
        convertFlexibleVarsToRigid(unifier, namedFunc);
        resolveFunc(unifier, namedFunc);
        detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
      }
    }

    private static void visitConstructor(PConstructor pConstructor) {
      var pStruct = pConstructor.pStruct();
      var sStructType = pStruct.type();
      var fieldSigs = sStructType.fields();
      var params = pStruct
          .fields()
          .list()
          .map(f -> new SItem(fieldSigs.get(f.name()).type(), f.fqn(), none(), f.location()));
      var sFuncType = new SFuncType(SItem.toTypes(params), sStructType);
      var schema = new SFuncSchema(list(), sFuncType);
      pConstructor.setSchema(schema);
      pConstructor.setSType(sFuncType);
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(PNamedFunc namedFunc)
        throws TypeException {
      var params = namedFunc.params();
      for (int i = 0; i < params.size(); i++) {
        var param = params.get(i);
        var index = i;
        param.defaultValueId().ifPresent(defaultValueId -> {
          var funcSchema = namedFunc.schema();
          var unifier = new Unifier();
          var resolvedParamType = funcSchema.type().params().elements().get(index);
          var paramType =
              replaceTypeVarsWithFlexible(funcSchema.typeParams(), resolvedParamType, unifier);
          var sSchema = scope().schemaFor(defaultValueId);
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

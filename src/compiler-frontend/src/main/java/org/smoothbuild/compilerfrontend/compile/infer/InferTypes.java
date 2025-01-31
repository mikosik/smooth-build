package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyNamedValue;
import static org.smoothbuild.compilerfrontend.compile.infer.FlexibleToRigidVarConverter.convertFlexibleVarsToRigid;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeResolver.resolveFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeResolver.resolveNamedValue;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PConstructor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScope;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.Constraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

public class InferTypes implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    try {
      new Worker(pModule.scope()).visitModule(pModule);
    } catch (TypeException e) {
      logger.log(e.log());
    }
    return output(pModule, COMPILER_FRONT_LABEL.append(":inferTypes"), logger.toList());
  }

  public static class Worker extends PModuleVisitor<TypeException> {
    private final PScope scope;

    private Worker(PScope scope) {
      this.scope = scope;
    }

    @Override
    public void visitStruct(PStruct pStruct) throws TypeException {
      inferStructType(pStruct);
    }

    private void inferStructType(PStruct struct) throws TypeException {
      NList<SItemSig> sItemSigs = struct.fields().map(this::inferFieldSig);
      var sStructType = new SStructType(struct.id(), sItemSigs);
      struct.setSType(sStructType);
    }

    private SItemSig inferFieldSig(PItem field) throws TypeException {
      var type = scope.translate(field.type());
      if (type.vars().isEmpty()) {
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
      unifyNamedValue(unifier, scope, namedValue);
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
        unifyFunc(unifier, scope, namedFunc);
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
          .map(f -> new SItem(fieldSigs.get(f.name()).type(), f.name(), none(), f.location()));
      var sFuncType = new SFuncType(SItem.toTypes(params), sStructType);
      var schema = new SFuncSchema(varSetS(), sFuncType);
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
              replaceVarsWithFlexible(funcSchema.quantifiedVars(), resolvedParamType, unifier);
          var sSchema = scope.schemaFor(defaultValueId);
          var defaultValueType = replaceQuantifiedVarsWithFlexible(sSchema, unifier);
          try {
            unifier.add(new Constraint(paramType, defaultValueType));
          } catch (UnifierException e) {
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(param.q(), resolvedParamType.q(), sSchema.type().q());
            throw new TypeException(compileError(param.location(), message), e);
          }
        });
      }
    }

    private static SType replaceQuantifiedVarsWithFlexible(SSchema sSchema, Unifier unifier) {
      return replaceVarsWithFlexible(sSchema.quantifiedVars(), sSchema.type(), unifier);
    }

    private static SType replaceVarsWithFlexible(SVarSet vars, SType type, Unifier unifier) {
      var mapping = vars.toList().toMap(v -> (SType) unifier.newFlexibleVar());
      return type.mapVars(mapping);
    }
  }
}

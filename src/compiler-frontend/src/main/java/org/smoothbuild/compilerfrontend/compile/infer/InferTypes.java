package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.ExprTypeUnifier.unifyNamedValue;
import static org.smoothbuild.compilerfrontend.compile.infer.TempVarsNamer.nameVarsInNamedFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.TempVarsNamer.nameVarsInNamedValue;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeInferrerResolve.resolveFunc;
import static org.smoothbuild.compilerfrontend.compile.infer.TypeInferrerResolve.resolveNamedValue;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

/**
 * Type inferring consists of
 *   - replacing not declared types with TempVar-s
 *   - walking expression tree unifying types with {@link ExprTypeUnifier}
 *   - resolving types from normalized {@link TypeInferrerResolve}
 */
public class InferTypes implements Task2<PModule, SScope, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule, SScope imported) {
    var logger = new Logger();
    var typeTeller = new TypeTeller(imported, pModule.scope());
    new Worker(typeTeller, logger).visitModule(pModule);
    return output(pModule, COMPILER_FRONT_LABEL.append(":inferTypes"), logger.toList());
  }

  public static class Worker extends PModuleVisitor {
    private final TypeTeller typeTeller;
    private final Logger logger;

    private Worker(TypeTeller typeTeller, Logger logger) {
      this.typeTeller = typeTeller;
      this.logger = logger;
    }

    @Override
    public void visitStruct(PStruct pStruct) {
      var sStructType = inferStructT(pStruct);
      sStructType.ifPresent(t -> visitConstructor(pStruct, t));
    }

    private void visitConstructor(PStruct pStruct, SStructType structT) {
      var constructorP = pStruct.constructor();
      var fieldSigs = structT.fields();
      var params = pStruct
          .fields()
          .list()
          .map(f -> new SItem(fieldSigs.get(f.name()).type(), f.name(), none(), f.location()));
      var funcTS = new SFuncType(SItem.toTypes(params), structT);
      var schema = new SFuncSchema(varSetS(), funcTS);
      constructorP.setSSchema(schema);
      constructorP.setSType(funcTS);
    }

    private Maybe<SStructType> inferStructT(PStruct struct) {
      return pullUpMaybe(struct.fields().list().map(this::inferFieldSig))
          .map(NList::nlist)
          .map(is -> new SStructType(struct.name(), is))
          .ifPresent(struct::setSType);
    }

    private Maybe<SItemSig> inferFieldSig(PItem field) {
      return typeTeller.translate(field.type()).flatMap(t -> {
        if (t.vars().isEmpty()) {
          field.setSType(t);
          return some(new SItemSig(t, field.name()));
        } else {
          var message = "Field type cannot be polymorphic. Found field %s with type %s."
              .formatted(field.q(), t.q());
          logger.log(compileError(field.type(), message));
          return none();
        }
      });
    }

    // value

    @Override
    public void visitNamedValue(PNamedValue namedValue) {
      inferNamedValue(namedValue);
    }

    private boolean inferNamedValue(PNamedValue namedValue) {
      var unifier = new Unifier();
      if (unifyNamedValue(unifier, typeTeller, logger, namedValue)) {
        nameVarsInNamedValue(unifier, namedValue);
        return resolveNamedValue(unifier, logger, namedValue);
      } else {
        return false;
      }
    }

    // func

    @Override
    public void visitNamedFunc(PNamedFunc namedFunc) {
      var unifier = new Unifier();
      var params = namedFunc.params();
      if (inferParamDefaultValues(params) && unifyFunc(unifier, typeTeller, logger, namedFunc)) {
        nameVarsInNamedFunc(unifier, namedFunc);
        if (resolveFunc(unifier, logger, namedFunc)) {
          detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
        }
      }
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(PNamedFunc namedFunc) {
      var params = namedFunc.params();
      for (int i = 0; i < params.size(); i++) {
        var param = params.get(i);
        var index = i;
        param.defaultValue().ifPresent(defaultValue -> {
          var schema = namedFunc.sSchema();
          var paramUnifier = new Unifier();
          var resolvedParamT = schema.type().params().elements().get(index);
          var paramT =
              replaceVarsWithTempVars(schema.quantifiedVars(), resolvedParamT, paramUnifier);
          var defaultValueType =
              replaceQuantifiedVarsWithTempVars(defaultValue.sSchema(), paramUnifier);
          try {
            paramUnifier.add(new EqualityConstraint(paramT, defaultValueType));
          } catch (UnifierException e) {
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(
                    param.q(), resolvedParamT.q(), defaultValue.sSchema().type().q());
            this.logger.log(compileError(defaultValue.location(), message));
          }
        });
      }
    }

    private static SType replaceQuantifiedVarsWithTempVars(SSchema sSchema, Unifier unifier) {
      return replaceVarsWithTempVars(sSchema.quantifiedVars(), sSchema.type(), unifier);
    }

    private static SType replaceVarsWithTempVars(SVarSet vars, SType type, Unifier unifier) {
      var mapping = vars.toList().toMap(v -> (SType) unifier.newTempVar());
      return type.mapVars(mapping);
    }

    // param default value

    private boolean inferParamDefaultValues(NList<PItem> params) {
      boolean result = true;
      for (var param : params) {
        result &= param.defaultValue().map(this::inferNamedValue).getOr(true);
      }
      return result;
    }
  }
}

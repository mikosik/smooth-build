package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseException;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.tool.EqualityConstraint;
import org.smoothbuild.compilerfrontend.lang.type.tool.Unifier;
import org.smoothbuild.compilerfrontend.lang.type.tool.UnifierException;

/**
 * Type inferring consists of
 *   - replacing not declared types with TempVar-s
 *   - walking expression tree unifying types with {@link ExprTypeUnifier}
 *   - inferring unit types {@link UnitTypeInferrer}
 *   - resolving types from normalized {@link TypeInferrerResolve}
 */
public class InferTypes implements Task2<PModule, SScope, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule, SScope environment) {
    var logger = new Logger();
    var typeTeller = new TypeTeller(environment, pModule.scope());
    new Worker(typeTeller, logger).visitModule(pModule);
    var label = COMPILER_FRONT_LABEL.append(":inferTypes");
    return output(pModule, label, logger.toList());
  }

  public static class Worker {
    private final Unifier unifier;
    private final TypeTeller typeTeller;
    private final Logger logger;

    private Worker(TypeTeller typeTeller, Logger logger) {
      this.unifier = new Unifier();
      this.typeTeller = typeTeller;
      this.logger = logger;
    }

    private void visitModule(PModule pModule) {
      pModule.structs().forEach(this::visitStruct);
      pModule.evaluables().forEach(this::visitReferenceable);
    }

    private void visitStruct(PStruct pStruct) {
      var structTS = inferStructT(pStruct);
      structTS.ifPresent(st -> visitConstructor(pStruct, st));
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
      constructorP.setSchemaS(schema);
      constructorP.setTypeS(funcTS);
    }

    private void visitReferenceable(PReferenceable pReferenceable) {
      switch (pReferenceable) {
        case PNamedFunc pNamedFunc -> visitFunc(pNamedFunc);
        case PNamedValue pNamedValue -> visitValue(pNamedValue);
        case PItem pItem -> throw unexpectedCaseException(pItem);
      }
    }

    private void visitValue(PNamedValue pNamedValue) {
      inferNamedValueSchema(pNamedValue);
    }

    private void visitFunc(PNamedFunc pNamedFunc) {
      inferNamedFuncSchema(pNamedFunc);
    }

    private Maybe<SStructType> inferStructT(PStruct struct) {
      return pullUpMaybe(struct.fields().list().map(this::inferFieldSig))
          .map(NList::nlist)
          .map(is -> new SStructType(struct.name(), is))
          .ifPresent(struct::setTypeS);
    }

    private Maybe<SItemSig> inferFieldSig(PItem field) {
      return typeTeller.translate(field.type()).flatMap(t -> {
        if (t.vars().isEmpty()) {
          field.setTypeS(t);
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

    private boolean inferNamedValueSchema(PNamedValue namedValue) {
      if (unifyNamedValue(namedValue)) {
        nameImplicitVars(namedValue);
        return resolveValueSchema(namedValue);
      } else {
        return false;
      }
    }

    private boolean unifyNamedValue(PNamedValue namedValue) {
      return new ExprTypeUnifier(unifier, typeTeller, logger).unifyNamedValue(namedValue);
    }

    private void nameImplicitVars(PNamedValue namedValue) {
      new TempVarsNamer(unifier).nameVarsInNamedValue(namedValue);
    }

    private boolean resolveValueSchema(PNamedValue pNamedValue) {
      return new TypeInferrerResolve(unifier, logger).resolveNamedValue(pNamedValue);
    }

    // func

    private void inferNamedFuncSchema(PNamedFunc namedFunc) {
      var params = namedFunc.params();
      if (inferParamDefaultValues(params) && unifyNamedFunc(namedFunc)) {
        nameImplicitVars(namedFunc);
        if (resolveNamedFunc(namedFunc)) {
          detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
        }
      }
    }

    private boolean unifyNamedFunc(PNamedFunc namedFunc) {
      return new ExprTypeUnifier(unifier, typeTeller, logger).unifyNamedFunc(namedFunc);
    }

    private void nameImplicitVars(PNamedFunc namedFunc) {
      new TempVarsNamer(unifier).nameVarsInNamedFunc(namedFunc);
    }

    private boolean resolveNamedFunc(PNamedFunc namedFunc) {
      return new TypeInferrerResolve(unifier, logger).resolveNamedFunc(namedFunc);
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(PNamedFunc namedFunc) {
      var params = namedFunc.params();
      for (int i = 0; i < params.size(); i++) {
        var param = params.get(i);
        var index = i;
        param.defaultValue().ifPresent(defaultValue -> {
          var schema = namedFunc.schemaS();
          var paramUnifier = new Unifier();
          var resolvedParamT = schema.type().params().elements().get(index);
          var paramT =
              replaceVarsWithTempVars(schema.quantifiedVars(), resolvedParamT, paramUnifier);
          var defaultValueType =
              replaceQuantifiedVarsWithTempVars(defaultValue.schemaS(), paramUnifier);
          try {
            paramUnifier.add(new EqualityConstraint(paramT, defaultValueType));
          } catch (UnifierException e) {
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(
                    param.q(), resolvedParamT.q(), defaultValue.schemaS().type().q());
            this.logger.log(compileError(defaultValue.location(), message));
          }
        });
      }
    }

    private static SType replaceQuantifiedVarsWithTempVars(SchemaS schemaS, Unifier unifier) {
      return replaceVarsWithTempVars(schemaS.quantifiedVars(), schemaS.type(), unifier);
    }

    private static SType replaceVarsWithTempVars(SVarSet vars, SType type, Unifier unifier) {
      var mapping = vars.toList().toMap(v -> (SType) unifier.newTempVar());
      return type.mapVars(mapping);
    }

    // param default value

    private boolean inferParamDefaultValues(NList<PItem> params) {
      return params.stream()
          .flatMap(p -> p.defaultValue().toList().stream())
          .allMatch(this::inferParamDefaultValue);
    }

    private boolean inferParamDefaultValue(PNamedValue defaultValue) {
      return inferNamedValueSchema(defaultValue);
    }
  }
}

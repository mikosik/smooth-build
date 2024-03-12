package org.smoothbuild.compilerfrontend.compile.infer;

import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.type.VarSetS.varSetS;

import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.lang.define.ItemS;
import org.smoothbuild.compilerfrontend.lang.define.ItemSigS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.lang.type.FuncSchemaS;
import org.smoothbuild.compilerfrontend.lang.type.FuncTS;
import org.smoothbuild.compilerfrontend.lang.type.SchemaS;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.lang.type.VarSetS;
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
public class InferTypes implements TryFunction<Tuple2<ModuleP, ScopeS>, ModuleP> {
  @Override
  public Try<ModuleP> apply(Tuple2<ModuleP, ScopeS> context) {
    var logger = new Logger();
    var moduleP = context.element1();
    var environment = context.element2();
    var typeTeller = new TypeTeller(environment, moduleP.scope());
    new Worker(typeTeller, logger).visitModule(moduleP);
    return Try.of(moduleP, logger);
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

    private void visitModule(ModuleP moduleP) {
      moduleP.structs().forEach(this::visitStruct);
      moduleP.evaluables().forEach(this::visitReferenceable);
    }

    private void visitStruct(StructP structP) {
      var structTS = inferStructT(structP);
      structTS.ifPresent(st -> visitConstructor(structP, st));
    }

    private void visitConstructor(StructP structP, StructTS structT) {
      var constructorP = structP.constructor();
      var fieldSigs = structT.fields();
      var params = structP
          .fields()
          .list()
          .map(f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), none(), f.location()));
      var funcTS = new FuncTS(ItemS.toTypes(params), structT);
      var schema = new FuncSchemaS(varSetS(), funcTS);
      constructorP.setSchemaS(schema);
      constructorP.setTypeS(funcTS);
    }

    private void visitReferenceable(ReferenceableP referenceableP) {
      switch (referenceableP) {
        case NamedFuncP namedFuncP -> visitFunc(namedFuncP);
        case NamedValueP namedValueP -> visitValue(namedValueP);
        case ItemP itemP -> throw new RuntimeException("shouldn't happen");
      }
    }

    private void visitValue(NamedValueP namedValueP) {
      inferNamedValueSchema(namedValueP);
    }

    private void visitFunc(NamedFuncP namedFuncP) {
      inferNamedFuncSchema(namedFuncP);
    }

    private Maybe<StructTS> inferStructT(StructP struct) {
      return pullUpMaybe(struct.fields().list().map(this::inferFieldSig))
          .map(NList::nlist)
          .map(is -> new StructTS(struct.name(), is))
          .ifPresent(struct::setTypeS);
    }

    private Maybe<ItemSigS> inferFieldSig(ItemP field) {
      return typeTeller.translate(field.type()).flatMap(t -> {
        if (t.vars().isEmpty()) {
          field.setTypeS(t);
          return some(new ItemSigS(t, field.name()));
        } else {
          var message = "Field type cannot be polymorphic. Found field %s with type %s."
              .formatted(field.q(), t.q());
          logger.log(compileError(field.type(), message));
          return none();
        }
      });
    }

    // value

    private boolean inferNamedValueSchema(NamedValueP namedValue) {
      if (unifyNamedValue(namedValue)) {
        nameImplicitVars(namedValue);
        return resolveValueSchema(namedValue);
      } else {
        return false;
      }
    }

    private boolean unifyNamedValue(NamedValueP namedValue) {
      return new ExprTypeUnifier(unifier, typeTeller, logger).unifyNamedValue(namedValue);
    }

    private void nameImplicitVars(NamedValueP namedValue) {
      new TempVarsNamer(unifier).nameVarsInNamedValue(namedValue);
    }

    private boolean resolveValueSchema(NamedValueP namedValueP) {
      return new TypeInferrerResolve(unifier, logger).resolveNamedValue(namedValueP);
    }

    // func

    private void inferNamedFuncSchema(NamedFuncP namedFunc) {
      var params = namedFunc.params();
      if (inferParamDefaultValues(params) && unifyNamedFunc(namedFunc)) {
        nameImplicitVars(namedFunc);
        if (resolveNamedFunc(namedFunc)) {
          detectTypeErrorsBetweenParamAndItsDefaultValue(namedFunc);
        }
      }
    }

    private boolean unifyNamedFunc(NamedFuncP namedFunc) {
      return new ExprTypeUnifier(unifier, typeTeller, logger).unifyNamedFunc(namedFunc);
    }

    private void nameImplicitVars(NamedFuncP namedFunc) {
      new TempVarsNamer(unifier).nameVarsInNamedFunc(namedFunc);
    }

    private boolean resolveNamedFunc(NamedFuncP namedFunc) {
      return new TypeInferrerResolve(unifier, logger).resolveNamedFunc(namedFunc);
    }

    private void detectTypeErrorsBetweenParamAndItsDefaultValue(NamedFuncP namedFunc) {
      var params = namedFunc.params();
      for (int i = 0; i < params.size(); i++) {
        var param = params.get(i);
        var index = i;
        param.defaultValue().ifPresent(defaultvalue -> {
          var schema = namedFunc.schemaS();
          var paramUnifier = new Unifier();
          var resolvedParamT = schema.type().params().elements().get(index);
          var paramT =
              replaceVarsWithTempVars(schema.quantifiedVars(), resolvedParamT, paramUnifier);
          var defaultValueType =
              replaceQuantifiedVarsWithTempVars(defaultvalue.schemaS(), paramUnifier);
          try {
            paramUnifier.add(new EqualityConstraint(paramT, defaultValueType));
          } catch (UnifierException e) {
            var message = "Parameter %s has type %s so it cannot have default value with type %s."
                .formatted(
                    param.q(), resolvedParamT.q(), defaultvalue.schemaS().type().q());
            this.logger.log(compileError(defaultvalue.location(), message));
          }
        });
      }
    }

    private static TypeS replaceQuantifiedVarsWithTempVars(SchemaS schemaS, Unifier unifier) {
      return replaceVarsWithTempVars(schemaS.quantifiedVars(), schemaS.type(), unifier);
    }

    private static TypeS replaceVarsWithTempVars(VarSetS vars, TypeS type, Unifier unifier) {
      var mapping = vars.toList().toMap(v -> (TypeS) unifier.newTempVar());
      return type.mapVars(mapping);
    }

    // param default value

    private boolean inferParamDefaultValues(NList<ItemP> params) {
      return params.stream()
          .flatMap(p -> p.defaultValue().toList().stream())
          .allMatch(this::inferParamDefaultValue);
    }

    private boolean inferParamDefaultValue(NamedValueP defaultValue) {
      return inferNamedValueSchema(defaultValue);
    }
  }
}

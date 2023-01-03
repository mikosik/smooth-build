package org.smoothbuild.compile.fs.ps.infer;

import static org.smoothbuild.compile.fs.lang.type.VarSetS.varSetS;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.ItemSigS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.SchemaS;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;
import org.smoothbuild.compile.fs.lang.type.VarSetS;
import org.smoothbuild.compile.fs.lang.type.tool.Unifier;
import org.smoothbuild.compile.fs.lang.type.tool.UnifierExc;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.RefableP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.collect.NList;

/**
 * Type inferring consists of
 *   - replacing not declared types with TempVar-s
 *   - walking expression tree converting types to normalized
 *       and unifying them {@link ExprTypeUnifier}
 *   - inferring unit types {@link UnitTypeInferrer}
 *   - resolving types from normalized {@link TypeInferrerResolve}
 */
public class TypeInferrer {
  private final Unifier unifier;
  private final TypeTeller typeTeller;
  private final Logger logger;

  public static LogBuffer inferTypes(ModuleP moduleP, ScopeS imported) {
    var logBuffer = new LogBuffer();
    var typeTeller = new TypeTeller(imported, moduleP.scope());
    new TypeInferrer(typeTeller, logBuffer)
        .visitModule(moduleP);
    return logBuffer;
  }

  private TypeInferrer(TypeTeller typeTeller, Logger logger) {
    this(new Unifier(), typeTeller, logger);
  }

  private TypeInferrer(Unifier unifier, TypeTeller typeTeller, Logger logger) {
    this.unifier = unifier;
    this.typeTeller = typeTeller;
    this.logger = logger;
  }

  private void visitModule(ModuleP moduleP) {
    moduleP.structs().forEach(this::visitStruct);
    moduleP.evaluables().forEach(this::visitRefable);
  }

  private void visitStruct(StructP structP) {
    var structTS = inferStructType(typeTeller, logger, structP);
    structTS.ifPresent(st -> visitConstructor(structP, st));
  }

  private void visitConstructor(StructP structP, StructTS structT) {
    var constructorP = structP.constructor();
    var fieldSigs = structT.fields();
    var params = structP.fields().map(
        f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), Optional.empty(), f.location()));
    var funcTS = new FuncTS(ItemS.toTypes(params), structT);
    var schema = new FuncSchemaS(varSetS(), funcTS);
    constructorP.setSchemaS(schema);
    constructorP.setTypeS(funcTS);
  }

  private void visitRefable(RefableP refableP) {
    switch (refableP) {
      case NamedFuncP namedFuncP -> visitFunc(namedFuncP);
      case NamedValueP namedValueP -> visitValue(namedValueP);
      case ItemP itemP -> throw new RuntimeException("shouldn't happen");
    }
  }

  private void visitValue(NamedValueP namedValueP) {
    new TypeInferrer(typeTeller, logger)
        .inferNamedValueSchema(namedValueP);
  }

  private void visitFunc(NamedFuncP namedFuncP) {
    new TypeInferrer(typeTeller, logger)
        .inferNamedFuncSchema(namedFuncP);
  }

  private Optional<StructTS> inferStructType(
      TypeTeller typeTeller, Logger logger, StructP struct) {
    return new TypeInferrer(typeTeller, logger)
        .inferStructT(struct);
  }

  private Optional<StructTS> inferStructT(StructP struct) {
    Optional<StructTS> structTS = pullUp(map(struct.fields().list(), this::inferFieldSig))
        .map(NList::nlist)
        .map(is -> new StructTS(struct.name(), is));
    structTS.ifPresent(struct::setTypeS);
    return structTS;
  }

  private Optional<ItemSigS> inferFieldSig(ItemP field) {
    return typeTeller.translate(field.type())
        .flatMap(t -> {
          if (t.vars().isEmpty()) {
            field.setTypeS(t);
            return Optional.of(new ItemSigS(t, field.name()));
          } else {
            var message = "Field type cannot be polymorphic. Found field %s with type %s."
                .formatted(field.q(), t.q());
            logger.log(compileError(field.type(), message));
            return Optional.empty();
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
    return new ExprTypeUnifier(unifier, typeTeller, logger)
        .unifyNamedValue(namedValue);
  }

  private void nameImplicitVars(NamedValueP namedValue) {
    new TempVarsNamer(unifier)
        .nameVarsInNamedValue(namedValue);
  }

  private boolean resolveValueSchema(NamedValueP namedValueP) {
    return new TypeInferrerResolve(unifier, logger)
        .resolveNamedValue(namedValueP);
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
    return new ExprTypeUnifier(unifier, typeTeller, logger)
        .unifyNamedFunc(namedFunc);
  }

  private void nameImplicitVars(NamedFuncP namedFunc) {
    new TempVarsNamer(unifier)
        .nameVarsInNamedFunc(namedFunc);
  }

  private boolean resolveNamedFunc(NamedFuncP namedFunc) {
    return new TypeInferrerResolve(unifier, logger)
        .resolveNamedFunc(namedFunc);
  }

  private void detectTypeErrorsBetweenParamAndItsDefaultValue(NamedFuncP namedFunc) {
    var params = namedFunc.params();
    for (int i = 0; i < params.size(); i++) {
      var param = params.get(i);
      var index = i;
      param.defaultValue().ifPresent(defaultvalue -> {
        var schema = namedFunc.schemaS();
        var paramUnifier = new Unifier();
        var resolvedParamT = schema.type().params().items().get(index);
        var paramT = replaceVarsWithTempVars(schema.quantifiedVars(), resolvedParamT, paramUnifier);
        var defaultValueType = replaceQuantifiedVarsWithTempVars(
            defaultvalue.schemaS(), paramUnifier);
        try {
          paramUnifier.unify(paramT, defaultValueType);
        } catch (UnifierExc e) {
          var message = "Parameter %s has type %s so it cannot have default value with type %s."
                  .formatted(param.q(), resolvedParamT.q(), defaultvalue.schemaS().type().q());
          this.logger.log(compileError(defaultvalue.location(), message));
        }
      });
    }
  }

  private static TypeS replaceQuantifiedVarsWithTempVars(SchemaS schemaS, Unifier unifier) {
    return replaceVarsWithTempVars(schemaS.quantifiedVars(), schemaS.type(), unifier);
  }

  private static TypeS replaceVarsWithTempVars(VarSetS vars, TypeS type, Unifier unifier) {
    var mapping = toMap(vars, v -> unifier.newTempVar());
    return type.mapVars(mapping);
  }

  // param default value

  private boolean inferParamDefaultValues(NList<ItemP> params) {
    return params.stream()
        .flatMap(p -> p.defaultValue().stream())
        .allMatch(this::inferParamDefaultValue);
  }

  private boolean inferParamDefaultValue(NamedValueP defaultValue) {
    return new TypeInferrer(new Unifier(), typeTeller, logger)
        .inferNamedValueSchema(defaultValue);
  }
}

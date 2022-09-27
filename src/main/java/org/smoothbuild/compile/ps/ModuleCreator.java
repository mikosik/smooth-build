package org.smoothbuild.compile.ps;

import static java.util.Optional.empty;
import static org.smoothbuild.compile.lang.define.ItemS.toTypes;
import static org.smoothbuild.compile.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferFuncSchema;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferStructType;
import static org.smoothbuild.compile.ps.infer.TypeInferrer.inferValSchema;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.util.bindings.ScopedBindings.innerScopeBindings;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.lang.define.NamedPolyEvaluableS;
import org.smoothbuild.compile.lang.define.StructDefS;
import org.smoothbuild.compile.lang.define.SyntCtorS;
import org.smoothbuild.compile.lang.define.TDefS;
import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.StructP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.NamedValP;
import org.smoothbuild.compile.ps.ast.refable.RefableP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.bindings.ScopedBindings;

public class ModuleCreator {
  private final ModPath path;
  private final ScopedBindings<Optional<TDefS>> types;
  private final ScopedBindings<Optional<NamedPolyEvaluableS>> bindings;
  private final LogBuffer logBuffer;
  private final PsTranslator psTranslator;

  public static Maybe<ModuleS> createModuleS(
      ModPath path, ModFiles modFiles, Ast ast, DefsS imported) {
    var logBuffer = new LogBuffer();
    var topTypes = newOptionalMutableBindings(imported.tDefs());
    var topEvaluables = newOptionalMutableBindings(imported.evaluables());
    var createTopObjsVisitor = new ModuleCreator(path, topTypes, topEvaluables, logBuffer);
    ast.structs().forEach(createTopObjsVisitor::visitStruct);
    ast.evaluables().forEach(createTopObjsVisitor::visitRefable);

    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    } else {
      var cast = innerScopeBindings(topEvaluables);
      var modS = new ModuleS(path, modFiles, innerScopeBindings(topTypes), cast);
      return maybe(modS, logBuffer);
    }
  }

  private static <T> ScopedBindings<Optional<T>> newOptionalMutableBindings(
      ImmutableBindings<T> tDefSImmutableBindings) {
    return new ScopedBindings<>(tDefSImmutableBindings.map(Optional::of));
  }

  private ModuleCreator(ModPath path, ScopedBindings<Optional<TDefS>> types,
      ScopedBindings<Optional<NamedPolyEvaluableS>> bindings, LogBuffer logBuffer) {
    this.path = path;
    this.types = types;
    this.bindings = bindings;
    this.logBuffer = logBuffer;
    this.psTranslator = new PsTranslator(bindings);
  }

  public void visitStruct(StructP struct) {
    Optional<StructTS> structTS = inferStructType(types, bindings, logBuffer, struct);
    Optional<TDefS> structDefS = structTS.map(s -> new StructDefS(s, path, struct.loc()));
    types.add(struct.name(), structDefS);
    var ctorS = structTS.map(st -> loadSyntCtor(path, struct, st));
    bindings.add(struct.ctor().name(), ctorS);
  }

  private static NamedPolyEvaluableS loadSyntCtor(ModPath path, StructP structP, StructTS structT) {
    var ctorP = structP.ctor();
    var name = ctorP.name();
    var fieldSigs = structT.fields();
    var params = structP.fields().map(
        f -> new ItemS(fieldSigs.get(f.name()).type(), f.name(), empty(), f.loc()));
    var funcTS = new FuncTS(structT, toTypes(params));
    var schema = new FuncSchemaS(funcTS.vars(), funcTS);
    var loc = structP.loc();
    return polyFuncS(schema, new SyntCtorS(funcTS, path, name, params, loc));
  }

  public void visitRefable(RefableP refableP) {
    switch (refableP) {
      case FuncP funcP -> visitFunc(funcP);
      case NamedValP namedValP -> visitValue(namedValP);
      case ItemP itemP -> throw new RuntimeException("shouldn't happen");
    }
  }

  public void visitValue(NamedValP namedValP) {
    var schema = inferValSchema(types, bindings, logBuffer, namedValP);
    var valS = schema.flatMap(s -> psTranslator.translateVal(path, namedValP, s.type()));
    bindings.add(namedValP.name(), valS);
  }

  public void visitFunc(FuncP funcP) {
    var schema = inferFuncSchema(types, bindings, logBuffer, funcP);
    var funcS = schema.flatMap(s -> psTranslator.translateFunc(path, funcP, s.type()));
    bindings.add(funcP.name(), funcS);
  }
}

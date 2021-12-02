package org.smoothbuild.lang.parse;

import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ReferenceResolver.resolveReferences;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.DefFuncS;
import org.smoothbuild.lang.base.define.DefTypeS;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.define.ItemS;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.ModFiles;
import org.smoothbuild.lang.base.define.ModPath;
import org.smoothbuild.lang.base.define.ModS;
import org.smoothbuild.lang.base.define.StructS;
import org.smoothbuild.lang.base.define.TopEvalS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.expr.CombineS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.ParamRefS;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.EvalN;
import org.smoothbuild.lang.parse.ast.StructN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ModLoader {
  private final TypeInferrer typeInferrer;
  private final TopEvalLoader topEvalLoader;
  private final TypeFactoryS typeFactory;

  @Inject
  public ModLoader(TypeInferrer typeInferrer, TopEvalLoader topEvalLoader,
      TypeFactoryS typeFactory) {
    this.typeInferrer = typeInferrer;
    this.topEvalLoader = topEvalLoader;
    this.typeFactory = typeFactory;
  }

  public Maybe<ModS> loadModule(
      ModPath path, ModFiles modFiles, String sourceCode, DefsS imported) {
    var logBuffer = new LogBuffer();
    FilePath filePath = modFiles.smoothFile();
    Maybe<ModContext> moduleContext = parseModule(filePath, sourceCode);
    logBuffer.logAll(moduleContext.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Ast ast = fromParseTree(filePath, moduleContext.value());
    logBuffer.logAll(analyzeSemantically(imported, ast));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Maybe<Ast> maybeSortedAst = ast.sortedByDeps();
    logBuffer.logAll(maybeSortedAst.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    Ast sortedAst = maybeSortedAst.value();

    resolveReferences(logBuffer, imported, sortedAst);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(typeInferrer.inferTypes(sortedAst, imported));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var modules = imported.modules().values().asList();
    var types = sortedAst.structs().map(s -> (DefTypeS) loadStruct(path, s));
    var evals = loadTopEvals(path, sortedAst);
    var moduleS = new ModS(path, modFiles, modules, types, evals);
    return maybeValueAndLogs(moduleS, logBuffer);
  }

  private StructS loadStruct(ModPath path, StructN struct) {
    var type = (StructTypeS) struct.type().get();
    var name = struct.name();
    var loc = struct.loc();
    return new StructS(type, path, name, loc);
  }

  private NList<TopEvalS> loadTopEvals(ModPath path, Ast ast) {
    var local = ImmutableList.<TopEvalS>builder();
    for (StructN struct : ast.structs()) {
      var ctorS = loadCtor(path, struct);
      local.add(ctorS);
    }
    for (EvalN eval : ast.topEvals()) {
      local.add(topEvalLoader.loadEvaluables(path, eval));
    }
    return nList(local.build());
  }

  private DefFuncS loadCtor(ModPath path, StructN struct) {
    var resultType = (StructTypeS) struct.type().get();
    var name = struct.ctor().name();
    var paramTypes = map(struct.fields(), f -> f.type().get());
    var type = typeFactory.func(resultType, paramTypes);
    var params = struct.fields().map(f -> f.toItem(path));
    var loc = struct.loc();
    var body = ctorBody(resultType, params, loc);
    return new DefFuncS(type, path, name, params, body, loc);
  }

  private CombineS ctorBody(StructTypeS resultType, NList<ItemS> params, Loc loc) {
    var paramRefs = map(params, p -> (ExprS) new ParamRefS(p.type(), p.name(), loc));
    return new CombineS(resultType, paramRefs, loc);
  }
}

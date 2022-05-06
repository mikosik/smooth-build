package org.smoothbuild.parse;

import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.parse.ParseModule.parseModule;
import static org.smoothbuild.parse.ReferenceResolver.resolveReferences;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefTypeS;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.StructS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.type.impl.StructTS;
import org.smoothbuild.lang.type.impl.TypeSF;
import org.smoothbuild.lang.type.impl.TypingS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.EvalN;
import org.smoothbuild.parse.ast.StructN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ModLoader {
  private final TypeInferrer typeInferrer;
  private final TopEvalLoader topEvalLoader;
  private final TypeSF typeSF;
  private final TypingS typing;

  @Inject
  public ModLoader(TypeInferrer typeInferrer, TopEvalLoader topEvalLoader,
      TypeSF typeSF, TypingS typing) {
    this.typeInferrer = typeInferrer;
    this.topEvalLoader = topEvalLoader;
    this.typeSF = typeSF;
    this.typing = typing;
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

    var types = sortedAst.structs().map(s -> (DefTypeS) loadStruct(path, s));
    var evals = loadTopEvals(path, sortedAst);
    var moduleS = new ModS(path, modFiles, types, evals);
    return maybeValueAndLogs(moduleS, logBuffer);
  }

  private StructS loadStruct(ModPath path, StructN struct) {
    var type = (StructTS) struct.type().get();
    var loc = struct.loc();
    return new StructS(type, path, loc);
  }

  private NList<TopEvalS> loadTopEvals(ModPath path, Ast ast) {
    var local = ImmutableList.<TopEvalS>builder();
    for (StructN struct : ast.structs()) {
      var ctorS = loadSyntCtor(path, struct);
      local.add(ctorS);
    }
    for (EvalN eval : ast.topEvals()) {
      local.add(topEvalLoader.loadEval(path, eval));
    }
    return nList(local.build());
  }

  private SyntCtorS loadSyntCtor(ModPath path, StructN struct) {
    var resultT = (StructTS) struct.type().get();
    var name = struct.ctor().name();
    var paramTs = map(struct.fields(), f -> f.type().get());
    var type = typeSF.func(varSetS(), resultT, paramTs);
    var params = struct.fields().map(f -> f.toItem(path));
    var loc = struct.loc();
    return new SyntCtorS(type, path, name, params, loc);
  }
}

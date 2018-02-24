package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.ConstructorLoader.loadConstructor;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Native;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.util.Maybe;

public class ModuleLoader {
  private final SRuntime runtime;
  private final AssignTypes assignTypes;
  private final FunctionLoader functionLoader;

  @Inject
  public ModuleLoader(SRuntime runtime, AssignTypes assignTypes, FunctionLoader functionLoader) {
    this.runtime = runtime;
    this.assignTypes = assignTypes;
    this.functionLoader = functionLoader;
  }

  public List<? extends Object> loadModule(Path script) {
    Maybe<ModuleContext> maybeModule = parseScript(script);
    if (!maybeModule.hasValue()) {
      return maybeModule.errors();
    }
    Ast ast = AstCreator.fromParseTree(script, maybeModule.value());
    List<? extends Object> errors = findSemanticErrors(runtime, ast);
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = ast.sortFuncsByDependencies(runtime.functions());
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = ast.sortTypesByDependencies(runtime.types());
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = assignTypes.assignTypes(runtime.functions(), ast);
    if (!errors.isEmpty()) {
      return errors;
    }
    errors = assignArgsToParams(runtime.functions(), ast);
    if (!errors.isEmpty()) {
      return errors;
    }
    Maybe<Map<String, Native>> maybeNatives = findNatives(changeExtension(script, "jar"));
    if (!maybeNatives.hasValue()) {
      return maybeNatives.errors();
    }
    Map<String, Native> natives = maybeNatives.value();
    errors = assignNatives(ast, natives);
    if (!errors.isEmpty()) {
      return errors;
    }
    loadFunctions(ast);
    return list();
  }

  private void loadFunctions(Ast ast) {
    for (StructNode struct : ast.structs()) {
      runtime.functions().add(loadConstructor(struct));
    }
    for (FuncNode func : ast.funcs()) {
      runtime.functions().add(functionLoader.loadFunction(runtime.functions(), func));
    }
  }
}

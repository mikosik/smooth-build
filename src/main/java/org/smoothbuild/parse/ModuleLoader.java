package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.AssignTypes.assignTypes;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.Accessor;
import org.smoothbuild.lang.function.Constructor;
import org.smoothbuild.lang.function.Native;
import org.smoothbuild.lang.function.Parameter;
import org.smoothbuild.lang.function.Signature;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FieldNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class ModuleLoader {
  private final SRuntime runtime;
  private final FunctionLoader functionLoader;

  @Inject
  public ModuleLoader(SRuntime runtime, FunctionLoader functionLoader) {
    this.runtime = runtime;
    this.functionLoader = functionLoader;
  }

  public List<? extends Object> loadModule(Path script) {
    Maybe<Map<String, Native>> natives = findNatives(changeExtension(script, "jar"));
    return parseScript(script)
        .mapValue(moduleContext -> AstCreator.fromParseTree(script, moduleContext))
        .invoke(ast -> findSemanticErrors(runtime, ast))
        .invoke(ast -> ast.sortFuncsByDependencies(runtime.functions()))
        .invoke(ast -> ast.sortTypesByDependencies(runtime.types()))
        .invoke(ast -> assignTypes(runtime, ast))
        .invoke(ast -> assignArgsToParams(runtime.functions(), ast))
        .invoke(natives, (ast, n) -> assignNatives(ast, n))
        .invokeConsumer(ast -> loadFunctions(ast))
        .errors();
  }

  private void loadFunctions(Ast ast) {
    for (StructNode struct : ast.structs()) {
      runtime.functions().add(loadConstructor(struct));
      for (FieldNode field : struct.fields()) {
        String name = struct.name() + "." + field.name();
        Parameter parameter = new Parameter(struct.get(Type.class), "struct", null);
        Signature signature = new Signature(field.get(Type.class), name, list(parameter));
        runtime.functions().add(new Accessor(signature, field.name(), field.location()));
      }
    }
    for (FuncNode func : ast.funcs()) {
      runtime.functions().add(functionLoader.loadFunction(runtime.functions(), func));
    }
  }

  private static Constructor loadConstructor(StructNode struct) {
    ImmutableList<Parameter> parameters = struct
        .fields()
        .stream()
        .map(f -> new Parameter(f.get(Type.class), f.name(), null))
        .collect(toImmutableList());
    Signature signature = new Signature(struct.get(Type.class), struct.name(), parameters);
    return new Constructor(signature, struct.location());
  }
}

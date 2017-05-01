package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

import com.google.common.collect.ImmutableSet;

public class FunctionContextCollector {
  public static Maybe<List<FunctionContext>> collectFunctionContexts(ModuleContext module,
      Functions functions) {
    Maybe<Map<Name, FunctionNode>> functionNodes = collectNodes(module, functions);
    Maybe<List<Name>> sorted = invoke(functionNodes, fns -> sortDependencies(functions, fns));
    return invokeWrap(functionNodes, sorted, (fns, s) -> sortFunctions(fns, s));
  }

  private static List<FunctionContext> sortFunctions(Map<Name, FunctionNode> functionNodes,
      List<Name> names) {
    return names.stream()
        .map(n -> functionNodes.get(n).context())
        .collect(toList());
  }

  private static Maybe<Map<Name, FunctionNode>> collectNodes(ModuleContext module,
      Functions functions) {
    Map<Name, FunctionNode> nodes = new HashMap<>();
    List<ParseError> errors = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunction(FunctionContext context) {
        FunctionNameContext nameContext = context.functionName();
        Name name = name(nameContext.getText());
        if (nodes.keySet().contains(name)) {
          errors.add(new ParseError(
              locationOf(nameContext), "Function " + name + " is already defined."));
          return null;
        }
        if (functions.contains(name)) {
          errors.add(new ParseError(locationOf(nameContext), "Function " + name
              + " cannot override builtin function with the same name."));
          return null;
        }
        visitChildren(context);
        nodes.put(name,
            new FunctionNode(name, context, currentDependencies, locationOf(nameContext)));
        return null;
      }

      public Void visitCall(CallContext call) {
        FunctionNameContext functionName = call.functionName();
        Name name = name(functionName.getText());
        CodeLocation location = locationOf(functionName);
        currentDependencies.add(new Dependency(location, name));
        return visitChildren(call);
      }

    }.visit(module);
    if (errors.isEmpty()) {
      return value(nodes).addErrors(undefinedFunctionErrors(functions, nodes));
    } else {
      return errors(errors);
    }
  }

  public static List<ParseError> undefinedFunctionErrors(Functions functions,
      Map<Name, FunctionNode> functionNodes) {
    Set<Dependency> defined = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(functionNodes.keySet())
        .build()
        .stream()
        .map(name -> new Dependency(null, name))
        .collect(toSet());
    Set<Dependency> referenced = functionNodes
        .values()
        .stream()
        .map(FunctionNode::dependencies)
        .flatMap(fd -> fd.stream())
        .collect(toSet());
    referenced.removeAll(defined);
    return referenced
        .stream()
        .map(FunctionContextCollector::unknownFunctionError)
        .collect(toList());
  }

  private static ParseError unknownFunctionError(Dependency dependency) {
    return new ParseError(dependency.location(),
        "Call to unknown function " + dependency.functionName() + ".");
  }
}

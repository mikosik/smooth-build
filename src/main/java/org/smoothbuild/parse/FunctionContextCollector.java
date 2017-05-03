package org.smoothbuild.parse;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.errors;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.value;
import static org.smoothbuild.util.Sets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.util.Lists;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableSet;

public class FunctionContextCollector {
  public static Maybe<List<FunctionContext>> collectFunctionContexts(ModuleContext module,
      Functions functions) {
    Maybe<Map<Name, FunctionNode>> functionNodes = collectNodes(module, functions);
    return invoke(functionNodes, fns -> sortedByDependencies(functions, fns));
  }

  private static Maybe<Map<Name, FunctionNode>> collectNodes(ModuleContext module,
      Functions functions) {
    Map<Name, FunctionNode> nodes = new HashMap<>();
    List<ParseError> errors = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunction(FunctionContext context) {
        NameContext nameContext = context.name();
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
        NameContext functionName = call.name();
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

    ImmutableSet<Name> all = ImmutableSet.<Name> builder()
        .addAll(functions.names())
        .addAll(functionNodes.keySet())
        .build();
    Set<Dependency> defined = map(all, name -> new Dependency(null, name));
    Set<Dependency> referenced = functionNodes
        .values()
        .stream()
        .map(FunctionNode::dependencies)
        .flatMap(fd -> fd.stream())
        .collect(toSet());
    referenced.removeAll(defined);
    return Lists.map(referenced, FunctionContextCollector::unknownFunctionError);
  }

  private static ParseError unknownFunctionError(Dependency dependency) {
    return new ParseError(dependency.location(),
        "Call to unknown function " + dependency.functionName() + ".");
  }

  public static Maybe<List<FunctionContext>> sortedByDependencies(Functions functions,
      Map<Name, FunctionNode> nodes) {
    Map<Name, FunctionNode> notSorted = new HashMap<>(nodes);
    Set<Name> availableFunctions = new HashSet<>(functions.names());
    List<Name> sorted = new ArrayList<>(nodes.size());
    DependencyStack stack = new DependencyStack();

    while (!notSorted.isEmpty() || !stack.isEmpty()) {
      if (stack.isEmpty()) {
        stack.push(removeNext(notSorted));
      }
      DependencyStackElem stackTop = stack.peek();
      Dependency missing = findUnreachableDependency(
          availableFunctions, sorted, stackTop.dependencies());
      if (missing == null) {
        sorted.add(stack.pop().name());
      } else {
        stackTop.setMissing(missing);
        FunctionNode next = notSorted.remove(missing.functionName());
        if (next == null) {
          return error(stack.createCycleError());
        } else {
          stack.push(new DependencyStackElem(next));
        }
      }
    }
    return value(Lists.map(sorted, n -> nodes.get(n).context()));
  }

  private static Dependency findUnreachableDependency(Set<Name> availableFunctions,
      List<Name> sorted, Set<Dependency> dependencies) {
    for (Dependency dependency : dependencies) {
      Name name = dependency.functionName();
      if (!(sorted.contains(name) || availableFunctions.contains(name))) {
        return dependency;
      }
    }
    return null;
  }

  private static DependencyStackElem removeNext(Map<Name, FunctionNode> dependencies) {
    Iterator<Entry<Name, FunctionNode>> it = dependencies.entrySet().iterator();
    Entry<Name, FunctionNode> element = it.next();
    it.remove();
    return new DependencyStackElem(element.getValue());
  }
}

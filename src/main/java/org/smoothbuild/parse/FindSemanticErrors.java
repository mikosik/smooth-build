package org.smoothbuild.parse;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.Sets.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.FunctionNode;
import org.smoothbuild.parse.ast.ParamNode;
import org.smoothbuild.parse.ast.TypeNode;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableSet;

public class FindSemanticErrors {
  public static List<ParseError> findSemanticErrors(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    errors.addAll(duplicateFunctionErrors(functions, ast));
    errors.addAll(undefinedFunctionErrors(functions, ast));
    errors.addAll(duplicateParamNameErrors(ast));
    errors.addAll(unknownParamTypeErrors(ast));
    errors.addAll(nestedArrayTypeParamErrors(ast));
    return errors;
  }

  private static List<ParseError> duplicateFunctionErrors(Functions functions,
      Ast ast) {
    Set<Name> defined = new HashSet<>();
    List<ParseError> errors = new ArrayList<>();
    for (FunctionNode node : ast.functions()) {
      Name name = node.name();
      if (defined.contains(name)) {
        errors.add(new ParseError(node.codeLocation(), "Function " + name
            + " is already defined."));
      }
      defined.add(name);
      if (functions.contains(name)) {
        errors.add(new ParseError(node.codeLocation(), "Function " + name
            + " cannot override builtin function with the same name."));
      }
    }
    return errors;
  }

  public static List<ParseError> undefinedFunctionErrors(Functions functions, Ast ast) {
    Map<Name, FunctionNode> functionNodes = ast.nameToFunctionMap();
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
    return Lists.map(referenced, FindSemanticErrors::unknownFunctionError);
  }

  private static ParseError unknownFunctionError(Dependency dependency) {
    return new ParseError(dependency.location(),
        "Call to unknown function " + dependency.functionName() + ".");
  }

  private static List<ParseError> duplicateParamNameErrors(Ast ast) {
    return ast.functions().stream()
        .map(f -> duplicateParamNameErrors(f.params()))
        .flatMap(errors -> errors.stream())
        .collect(toList());
  }

  private static List<ParseError> duplicateParamNameErrors(List<ParamNode> params) {
    List<ParseError> errors = new ArrayList<>();
    Set<String> names = new HashSet<>();
    for (ParamNode node : params) {
      String name = node.name();
      if (names.contains(name)) {
        errors.add(new ParseError(node.codeLocation(), "Duplicate parameter '" + name + "'."));
      }
      names.add(name);
    }
    return errors;
  }

  private static List<ParseError> unknownParamTypeErrors(Ast ast) {
    return ast.functions().stream()
        .map(f -> unknownParamTypeErrors(f.params()))
        .flatMap(errors -> errors.stream())
        .collect(toList());
  }

  private static List<ParseError> unknownParamTypeErrors(List<ParamNode> params) {
    List<ParseError> errors = new ArrayList<>();
    for (ParamNode node : params) {
      TypeNode type = node.typeNode();
      while (type instanceof ArrayTypeNode) {
        type = ((ArrayTypeNode) type).elementType();
      }
      if (Types.basicTypeFromString(type.name()) == null) {
        errors.add(new ParseError(type.codeLocation(), "Unknown type '" + type.name() + "'."));
      }
    }
    return errors;
  }

  private static List<ParseError> nestedArrayTypeParamErrors(Ast ast) {
    return ast.functions().stream()
        .map(f -> nestedArrayTypeParamErrors(f.params()))
        .flatMap(errors -> errors.stream())
        .collect(toList());
  }

  private static List<ParseError> nestedArrayTypeParamErrors(List<ParamNode> params) {
    List<ParseError> errors = new ArrayList<>();
    for (ParamNode node : params) {
      TypeNode type = node.typeNode();
      if (type instanceof ArrayTypeNode
          && ((ArrayTypeNode) type).elementType() instanceof ArrayTypeNode) {
        errors.add(new ParseError(node.codeLocation(), "Nested array type is forbidden."));
      }
    }
    return errors;
  }
}

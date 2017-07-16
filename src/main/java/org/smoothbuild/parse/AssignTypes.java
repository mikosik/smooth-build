package org.smoothbuild.parse;

import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.commonSuperType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ArrayNode;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ExprNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StringNode;
import org.smoothbuild.parse.ast.TypeNode;

public class AssignTypes {
  public static List<ParseError> assignTypes(Functions functions, Ast ast) {
    Map<Name, Type> functionTypes = functions
        .nameToFunctionMap()
        .entrySet()
        .stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().type()));
    return new ErrorAstWalker() {
      public List<ParseError> visitFunctions(List<FuncNode> functions) {
        List<ParseError> errors = new ArrayList<>();
        for (FuncNode funcNode : functions) {
          errors.addAll(visitFunction(funcNode));
          if (funcNode.has(Type.class)) {
            functionTypes.put(funcNode.name(), funcNode.get(Type.class));
          }
        }
        return errors;
      }

      public List<ParseError> visitFunction(FuncNode function) {
        List<ParseError> errors = super.visitFunction(function);
        function.set(Type.class, function.expr().get(Type.class));
        return errors;
      }

      public List<ParseError> visitType(TypeNode type) {
        type.set(Type.class, createType(type));
        return super.visitType(type);
      }

      public List<ParseError> visitArray(ArrayNode array) {
        List<ParseError> errors = visitElements(array.elements(), this::visitExpr);
        CodeLocation location = array.codeLocation();
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          array.set(Type.class, NIL);
          return errors;
        }
        Type firstType = expressions.get(0).get(Type.class);
        if (firstType == null) {
          array.set(Type.class, null);
          return errors;
        }
        Type superType = firstType;

        for (int i = 1; i < expressions.size(); i++) {
          Type type = expressions.get(i).get(Type.class);
          if (type == null) {
            return errors;
          }
          superType = commonSuperType(superType, type);

          if (superType == null) {
            array.set(Type.class, null);
            errors.add(new ParseError(location,
                "Array cannot contain elements of incompatible types.\n"
                    + "First element has type " + firstType + " while element at index " + i
                    + " has type " + type + "."));
            return errors;
          }
        }
        ArrayType arrayType = Types.arrayOf(superType);
        if (arrayType == null) {
          array.set(Type.class, null);
          errors.add(new ParseError(location, "Array cannot contain element with type "
              + superType + ". Only following types are allowed: " + Types.basicTypes()
              + "."));
          return errors;
        }
        array.set(Type.class, arrayType);
        return errors;
      }

      public List<ParseError> visitCall(CallNode call) {
        if (functionTypes.containsKey(call.name())) {
          call.set(Type.class, functionTypes.get(call.name()));
        }
        return visitArgs(call.args());
      }

      public List<ParseError> visitArg(ArgNode arg) {
        ExprNode expr = arg.expr();
        List<ParseError> errors = visitExpr(expr);
        arg.set(Type.class, expr.get(Type.class));
        return errors;
      }

      public List<ParseError> visitString(StringNode expr) {
        expr.set(Type.class, STRING);
        return reduceIdentity();
      }
    }.visitAst(ast);
  }

  private static Type createType(TypeNode typeNode) {
    if (typeNode instanceof ArrayTypeNode) {
      TypeNode elementTypeNode = ((ArrayTypeNode) typeNode).elementType();
      return Types.arrayOf(createType(elementTypeNode));
    }
    return Types.basicTypeFromString(typeNode.name());
  }
}

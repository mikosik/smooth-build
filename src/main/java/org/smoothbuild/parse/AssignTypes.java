package org.smoothbuild.parse;

import java.util.List;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.ast.ArrayTypeNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.TypeNode;

public class AssignTypes {
  public static List<ParseError> assignTypes(Ast ast) {
    return new ErrorAstWalker() {
      public List<ParseError> visitType(TypeNode type) {
        type.set(Type.class, createType(type));
        return super.visitType(type);
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

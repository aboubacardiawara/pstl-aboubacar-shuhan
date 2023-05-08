package main.exporter.implem.codeGeneration;

import java.util.ArrayList;
import java.util.List;

import main.adaptation.RUASTNodeType;
import main.adaptation.interfaces.IRUAST;
import main.util.Utile;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Modifier;

public class CodeBuilderFromRUAST {

    public static String buildFrom(IRUAST ruast) {
        return getClassCode(ruast);

    }

    private static String getClassCode(IRUAST ruast) {
        StringBuilder classCodeBuilder = new StringBuilder();
        classCodeBuilder.append(getClassSignature(ruast));
        classCodeBuilder.append("{\n");
        classCodeBuilder.append(getClassBodyCode(ruast));
        classCodeBuilder.append("}");

        return classCodeBuilder.toString();
    }

    /**
     * Calcul le code source associé à une classe.
     * Cela comprend le code source:
     * - de ses attributs
     * - de ses méthodes
     */
    private static String getClassBodyCode(IRUAST ruast) {
        StringBuilder classBodyBuilder = new StringBuilder();
        ruast.getChildren().forEach(
                child -> classBodyBuilder.append(dispath(child) + "\n"));
        return classBodyBuilder.toString();
    }

    /**
     * Calcul la signture associée à un noeud de type class.
     * 
     * @param ruast
     * @return
     */
    private static String getClassSignature(IRUAST ruast) {
        ASTNode jdtNode = ruast.getRoot().getJdtNode();
        if (jdtNode == null || !(jdtNode instanceof TypeDeclaration)) {
            return null;
        }
        TypeDeclaration typeDecl = (TypeDeclaration) jdtNode;

        StringBuilder sb = new StringBuilder();

        // get the modifier
        int modifiers = typeDecl.getModifiers();

        if (Modifier.isPublic(modifiers)) {
            sb.append("public");
        } else if (Modifier.isProtected(modifiers)) {
            sb.append("protected");
        } else if (Modifier.isPrivate(modifiers)) {
            sb.append("private");
        } else {
            sb.append("");
        }

        if (Modifier.isAbstract(modifiers)) {
            sb.append(" abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            sb.append(" final");
        }

        if (typeDecl.isInterface()) {
            sb.append(" interface ");
        } else {
            sb.append(" class ");
        }
        sb.append(Utile.buildClassName(ruast));

        // get the father class and the implements
        Type superclassType = typeDecl.getSuperclassType();
        if (superclassType != null) {
            String extendsName = superclassType.toString();
            sb.append(" extends " + extendsName);
        }
        List<Type> superInterfaces = typeDecl.superInterfaceTypes();
        if (!superInterfaces.isEmpty()) {
            sb.append(" implements ");
            for (int i = 0; i < superInterfaces.size(); i++) {
                sb.append(superInterfaces.get(i).toString());
                if (i < superInterfaces.size() - 1) {
                    sb.append(", ");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Gère suivant si on a un attribut ou une méthode
     * la méthode à appeler pour l'extraction du code source.
     * 
     * @param ruast
     * @return
     */
    private static String dispath(IRUAST ruast) {
        if (ruast.getRoot().getType() == RUASTNodeType.FIELD) {
            return getFieldSourceCode(ruast);
        }
        return getMethodSourceCode(ruast);
    }

    /**
     * Construit le code source d'une méthode
     * 
     * @param ruast
     * @return
     */
    private static String getMethodSourceCode(IRUAST ruast) {
        StringBuilder methodBodyBuilder = new StringBuilder();
        methodBodyBuilder.append(getMethodSignature(ruast.getRoot().getJdtNode()));
        methodBodyBuilder.append("{\n");
        ruast.getChildren().forEach(
                child -> {
                    String code = getInstructionSourceCode(child);
                    methodBodyBuilder.append(code);
                });

        methodBodyBuilder.append("}");
        return methodBodyBuilder.toString();
    }

    public static String getMethodSignature(ASTNode astNode) {
        StringBuilder methodSignature = new StringBuilder();

        if (astNode instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) astNode;

            // Get the modifiers of the method
            List<IExtendedModifier> modifiers = methodDeclaration.modifiers();
            if (modifiers != null) {
                for (IExtendedModifier modifier : modifiers) {
                    if (modifier instanceof Modifier) {
                        methodSignature.append(((Modifier) modifier).getKeyword().toString()).append(" ");
                    } else if (modifier instanceof Annotation) {
                        methodSignature.append(((Annotation) modifier).toString()).append(" ");
                    }
                }
            }

            // Get the return type
            Type returnType = methodDeclaration.getReturnType2();
            if (returnType != null) {
                methodSignature.append(returnType.toString()).append(" ");
            }

            // Get the name of the method
            String methodName = methodDeclaration.getName().toString();
            methodSignature.append(methodName).append("(");

            // Get the parameters and process them
            List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
            if (parameters != null) {
                for (SingleVariableDeclaration parameter : parameters) {
                    // Get the type and name of the parameter
                    Type parameterType = parameter.getType();
                    String parameterName = parameter.getName().toString();

                    // Add the type and name of the parameter to the method signature
                    methodSignature.append(parameterType.toString()).append(" ").append(parameterName).append(", ");
                }

                // Remove the final comma and space from the parameter list
                if (parameters.size() > 0) {
                    methodSignature.delete(methodSignature.length() - 2, methodSignature.length());
                }
            }

            methodSignature.append(")");
        }

        return methodSignature.toString();
    }

    /**
     * Construit le code source associé à une
     * instruction. Une méthode est composée de
     * plusieurs instructions.
     * 
     * @param ruast
     * @return
     */
    private static String getInstructionSourceCode(IRUAST ruast) {
        // String sourceCode = ruast.getRoot().getJdtNode().toString();
        String sourceCode = ruast.getName();
        return sourceCode;
    }

    /**
     * construit le code source d'un attribut
     * 
     * @param ruast
     * @return
     */
    private static String getFieldSourceCode(IRUAST ruast) {
        return ruast.getRoot().getJdtNode().toString();
    }

}

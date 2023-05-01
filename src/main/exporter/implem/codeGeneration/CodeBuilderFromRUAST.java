package main.exporter.implem.codeGeneration;

import java.util.List;

import main.adaptation.RUASTNodeType;
import main.adaptation.interfaces.IRUAST;
import main.util.Utile;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Type;
import java.lang.reflect.Modifier;

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
        // get the name
        String className = Utile.buildClassName(ruast);
        sb.append(className);

        // get the modifier
        int modifiers = typeDecl.getModifiers();

        if (Modifier.isPublic(modifiers)) {
            sb.append("public");
        } else if (Modifier.isProtected(modifiers)) {
            sb.append("protected");
        } else if (Modifier.isPrivate(modifiers)) {
            sb.append("private");
        } else {
            throw new NotImplementedException("[Method modifier] Unhandled case: " + modifiers);
        }

        if (Modifier.isAbstract(modifiers)) {
            sb.append("abstract");
        }
        if (Modifier.isFinal(modifiers)) {
            sb.append("final");
        }

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
        methodBodyBuilder.append(ruast.getName());
        methodBodyBuilder.append("{\n");
        ruast.getChildren().forEach(
                child -> methodBodyBuilder.append(getInstructionSourceCode(child) + "\n"));

        methodBodyBuilder.append("}");
        return methodBodyBuilder.toString();
    }

    private static String getInstructionSourceCode(IRUAST ruast) {
        return ruast.getName();
    }

    /**
     * construit le code source d'un attribut
     * 
     * @param ruast
     * @return
     */
    private static String getFieldSourceCode(IRUAST ruast) {
        return ruast.getName();
    }

}

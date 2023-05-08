package main.exporter.implem.codeGeneration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.internal.resources.Folder;
import java.nio.file.Path;
import org.apache.commons.lang.NotImplementedException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.Modifier;

import main.adaptation.RUASTNodeType;
import main.adaptation.interfaces.IRUAST;
import main.exporter.IExporter;
import main.util.Utile;

public class JAVACodeExporter implements IExporter {

    protected String folderPath;

    public JAVACodeExporter(String folderPath) {
        super();
        this.folderPath = folderPath;
    }

    @Override
    public void export(IRUAST ruast) {
        createOnlyClassFiles(ruast);
    }

    /**
     * 
     */
    protected void createOnlyClassFiles(IRUAST ruast) {
        createFolder();
        ruast.getChildren().forEach(child -> {
            if (shouldBeGenerated(child)) {
                createFileFromRUAST(child);
            }
        });
    }

    protected void createFileFromRUAST(IRUAST ruast) {
        assert ruast.getRoot().getType() == RUASTNodeType.TYPE_DEFINITION : "Should be a type definition";
        String fileName = Utile.buildClassName(ruast) + ".java";
        Path filePath = Paths.get(this.folderPath + "/" + fileName);
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            writeSourceCode(filePath, ruast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeSourceCode(Path filePath, IRUAST ruast) throws IOException {
        Writer writer = new FileWriter(filePath.toString());
        String javaCode = generateCode(ruast);
        writer.write(javaCode);
        writer.close();
    }

    /**
     * Create folder and juste files
     */
    protected void createFolder() {
        try {
            Path path = Paths.get(this.folderPath);
            Files.createDirectory(path);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    protected String generateCode(IRUAST ruast) {
        return getClassCode(ruast);
    }

    protected String getClassCode(IRUAST ruast) {
        StringBuilder classCodeBuilder = new StringBuilder();
        classCodeBuilder.append(getClassSignature(ruast));
        classCodeBuilder.append("{\n");
        classCodeBuilder.append(getClassBodyCode(ruast));
        classCodeBuilder.append("}");

        return classCodeBuilder.toString();
    }

    /**
     * Calcul la signture associée à un noeud de type class.
     * 
     * @param ruast
     * @return
     */
    protected static String getClassSignature(IRUAST ruast) {
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
     * Calcul le code source associé à une classe.
     * Cela comprend le code source:
     * - de ses attributs
     * - de ses méthodes
     */
    protected String getClassBodyCode(IRUAST ruast) {
        StringBuilder classBodyBuilder = new StringBuilder();
        ruast.getChildren().forEach(
            child -> classBodyBuilder.append(dispath(child) + "\n"));
        return classBodyBuilder.toString();
    }

    /**
     * Gère suivant si on a un attribut ou une méthode
     * la méthode à appeler pour l'extraction du code source.
     * 
     * @param ruast
     * @return
     */
    protected String dispath(IRUAST ruast) {
        if (ruast.getRoot().getType() == RUASTNodeType.FIELD) {
            return getFieldSourceCode(ruast);
        }
        return getMethodSourceCode(ruast);
    }

    /**
     * construit le code source d'un attribut
     * 
     * @param ruast
     * @return
     */
    protected String getFieldSourceCode(IRUAST ruast) {
        if (shouldBeGenerated(ruast)) {
            return ruast.getRoot().getJdtNode().toString();
        }
        return "\n";
    }

    /**
     * Construit le code source d'une méthode
     * 
     * @param ruast
     * @return
     */
    protected String getMethodSourceCode(IRUAST ruast) {
        if (!shouldBeGenerated(ruast)) {
            return "\n";
        }
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

    protected static String getMethodSignature(ASTNode astNode) {
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
    protected String getInstructionSourceCode(IRUAST ruast) {
        if (!shouldBeGenerated(ruast)) {
            return "\n";
        }
        String sourceCode = ruast.getRoot().getJdtNode().toString();
        // String sourceCode = ruast.getName();
        return sourceCode;
    }

    /**
     * Verifie si le code asscoié à un noeud soit etre genere.
     * Dans le cas present, tout code doit etre genere.
     * 
     * @param node
     * @return {boolean}
     */
    protected boolean shouldBeGenerated(IRUAST node) {
        return true;
    }
}

package main.exporter.implem.codeGeneration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import java.nio.file.Path;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;

import main.exporter.IExporter;
import main.ruast.impl.RUASTNodeType;
import main.ruast.interfaces.IRUAST;
import main.util.Utile;

public class JAVACodeExporter implements IExporter {

    private static final String TAB_CHAR = "    ";
    private static final boolean VERBOSE = false;
    protected String folderPath;
    protected boolean shoulGenAllFeatures;
    private int currentLineNum = 1;

    /** Utile pour generer les fichiers de configuration de mobioseforge */
    protected String fileName = "";
    private String currentFileName = "";

    public JAVACodeExporter(String folderPath) {
        super();
        this.folderPath = folderPath;
        this.shoulGenAllFeatures = false;
    }

    @Override
    public void export(IRUAST ruast) {
        display("Exporting...");
        createFiles(ruast);
        display("Exporting done");
    }

    /**
     * 
     */
    protected void createFiles(IRUAST ruast) {
        createFolder();
        ruast.getChildren().forEach(fileruast -> {
            if (shouldBeGenerated(fileruast)) {
                createFileFromRUAST(fileruast);
            }
        });
    }

    /**
     * Crée un fichier java à partir d'un RUAST
     * 
     * format du nom du fichier: [package.]className
     * 
     * @param ruast
     */
    protected void createFileFromRUAST(IRUAST ruast) {
        assert ruast.getRoot().getType() == RUASTNodeType.FILE : "Should be a File RUAST";
        String fileName = extractClassName(ruast) + ".java";
        this.currentFileName = this.folderPath + "/" + fileName;
        String packageName = extractPackageName(ruast);
        Path filePath = Paths.get(this.folderPath + "/" + packageName + fileName);
        try {
            Files.deleteIfExists(filePath);
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            writeSourceCode(filePath, ruast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrait le nom de la classe à partir d'un RUAST
     * format: [packageName.]className.Java
     * On veut la partie correspondant à className
     * 
     * @param ruast
     * @return
     */
    private String extractPackageName(IRUAST ruast) {
        String[] fileNameParts = ruast.getName().split("\\.");
        if (fileNameParts.length == 1) { // dans ce cas c'est le package par defaut
            return "";
        }

        // sinon le package correspont à la concatenation des n-1 premiers elements
        StringBuilder packageNameBuilder = new StringBuilder();
        for (int i = 0; i < fileNameParts.length - 2; i++) {
            packageNameBuilder.append(fileNameParts[i]);
            packageNameBuilder.append("/");
        }
        return packageNameBuilder.toString() + "/";
    }

    

    /**
     * Extrait le nom de la classe à partir d'un RUAST
     * format: [packageName.]className.Java
     * On veut la partie correspondant à className
     * 
     * @param ruast
     * @return
     */
    private String extractClassName(IRUAST ruast) {
        String[] fileNameParts = ruast.getName().split("\\.");
        if (fileNameParts.length == 0) { // dans ce cas c'est le package par defaut
            return fileNameParts[0];
        }
        String className = fileNameParts[fileNameParts.length - 2];
        return className;
    }

    /**
     * Construit le code source de toute une classe à partir d'un RUAST.
     * Il prend un noeud correspondant à un fichier en paramètre.
     * Et reconstruit le code source de toutes les classes qu'il contient.
     * On s'arrange à conserver les positions de toutes les unités de code.
     * Elles seront utiles pour visualiser les fonctionnalités dans mobioseforge.
     * @param filePath le chemin du fichier à écrire
     * @param ruast le RUAST correspondant au fichier
     * @throws IOException 
     */
    protected void writeSourceCode(Path filePath, IRUAST ruast) throws IOException {
        assert ruast.getRoot().getType() == RUASTNodeType.FILE : "Should be a File RUAST";
        
        Writer writer = new FileWriter(filePath.toString());
        display("Writing file: " + filePath.toString());
        this.fileName = filePath.toString();

        // write package declaration
        writePackageDeclaration(ruast, writer);

        // les import
        writeImportDeclarations(ruast, writer);

        
        // On écrit les classes
        writeClassesDefinition(ruast, writer);

        writer.close();
    }

    /**
     * Ecriture des declarations de classes.
     * @param ruast
     * @param writer
     */
    private void writeClassesDefinition(IRUAST ruast, Writer writer) {
        assert ruast.getRoot().getType() == RUASTNodeType.FILE : "Should be a File RUAST";
        ruast.getChildren()
        .stream()
        .filter(child -> child.getRoot().getType() == RUASTNodeType.TYPE_DEFINITION)
        .forEach(child -> writeClass(writer, child));
    }

    /**
     * Ecrit toute les instructions d'importation d'un fichier.
     * Ajuste le numéro de ligne courant en fonction du nombre d'import.
     * @param ruast
     * @param writer
     */
    private void writeImportDeclarations(IRUAST ruast, Writer writer) {
        ruast.getChildren()
        .stream()
        .filter(child -> child.getRoot().getType() == RUASTNodeType.STATEMENT)
        .forEach(child -> writeImport(writer, child));

        // on ajuste le numéro de ligne courant en ajoutant le nombre d'imports
        this.currentLineNum += ruast.getChildren().size();
    }

    private void writePackageDeclaration(IRUAST ruast, Writer writer) throws IOException {
        String packageDeclaration = buildPackageDeclaration(ruast);
        if (packageDeclaration != "") {
            this.currentLineNum++;
        }
        writer.write(packageDeclaration);
    }

    private String buildPackageDeclaration(IRUAST ruast) {
        
        String packageName = extractPackageName(ruast);
        if (packageName.equals("/")) {
            return "";
        }
        packageName = packageName.substring(0, packageName.length() - 2);
        return "package " + packageName + ";\n\n";
    }

    private void writeImport(Writer writer, IRUAST child) {
        assert fileName != "" : "Le chemin du fichier d'ecriture doit avoir ete initialise";
        if (shouldBeGenerated(child)) {
            String code = getImportDeclarationCode(child);
            try {
                writer.write(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generation du code d'une classe.
     * On adapte la ligne courante 
     * @param writer
     * @param ruast
     */
    private void writeClass(Writer writer, IRUAST ruast) {
        display("[start] class line: " + currentLineNum);
        ruast.setFileName(currentFileName);
        ruast.setStartLine(currentLineNum);
        if (shouldBeGenerated(ruast)) {
            String code = getClassCode(ruast);
            // maj du numéro de ligne courant
            try {
                writer.write(code);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        display("[end] class line: " + currentLineNum);
        ruast.setEndLine(currentLineNum);
    }


    protected CompilationUnit compilationUnitFromStr(String sourceCode) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(sourceCode.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
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

    private String getImportDeclarationCode(IRUAST ruast) {
        return ruast.getName();
    }

    protected String getClassCode(IRUAST ruast) {
        StringBuilder classCodeBuilder = new StringBuilder();
        classCodeBuilder.append(getClassSignature(ruast));
        classCodeBuilder.append("{\n");
        this.currentLineNum++;
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
            display("[start] Field: " + this.currentLineNum);
            ruast.setFileName(currentFileName);
            ruast.setStartLine(currentLineNum);
            return TAB_CHAR + getFieldSourceCode(ruast);
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
            String code = ruast.getRoot().getJdtNode().toString();
            this.currentLineNum += 1 + code.split("\n").length;
            return code;
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
        display("---> " + currentFileName);
        if (!shouldBeGenerated(ruast)) {
            return "\n";
        }
        StringBuilder methodBodyBuilder = new StringBuilder();
        display("[start] Method: " + this.currentLineNum);
        ruast.setFileName(currentFileName);
        ruast.setStartLine(currentLineNum);
        methodBodyBuilder.append(TAB_CHAR + getMethodSignature(ruast.getRoot().getJdtNode()));
        methodBodyBuilder.append("{\n");
        this.currentLineNum++;
        ruast.getChildren().forEach(
                child -> {
                    String code = getInstructionSourceCode(child);

                    this.currentLineNum += code.split("\n").length;
                    int endLine = this.currentLineNum-1;
                    display("[End] Instruction: " + endLine);
                    child.setEndLine(endLine);
                    methodBodyBuilder.append(formatMethodInstruction(code));
                });

        methodBodyBuilder.append(TAB_CHAR + "}\n");
        display("[end] Method: " + this.currentLineNum);
        ruast.setEndLine(currentLineNum);
        this.currentLineNum += 2;
        return methodBodyBuilder.toString();
    }

    private String formatMethodInstruction(String instruction) {
        String instructionWithoutLastLineBreak = instruction.substring(0, instruction.length() - 1);
        return TAB_CHAR + TAB_CHAR + instructionWithoutLastLineBreak.replaceAll("\n", "\n" + TAB_CHAR + TAB_CHAR) + "\n";
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
        display("[start] Instruction: " + this.currentLineNum);
        ruast.setFileName(currentFileName);
        ruast.setStartLine(currentLineNum);
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

    @Override
    public void generateMaximalCode() {
        this.shoulGenAllFeatures = true;
    }


    private void display(String string) {
        if (VERBOSE) {
            System.out.println(string);
        }
    }

}

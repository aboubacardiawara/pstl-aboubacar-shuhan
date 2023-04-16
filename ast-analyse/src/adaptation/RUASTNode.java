package adaptation;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import adaptation.interfaces.IRUASTNode;

public class RUASTNode implements IRUASTNode {

    protected String name;
    protected int id;
    protected Variants variants;
    private RUASTNodeType type;

    public RUASTNode(ASTNode root, int nodeId, Integer variant, RUASTNodeType nodeType) {
        name = getASTNodeName(root);
        id = nodeId;
        variants = new Variants();
        variants.add(variant);
        type = nodeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Variants getVariants() {
        return variants;
    }

    @Override
    public RUASTNodeType getType() {
        return type;
    }

    private String getASTNodeName(ASTNode node) {
        String name = null;
        switch (node.getNodeType()) {
            case ASTNode.PACKAGE_DECLARATION:
                String packageName = ((PackageDeclaration) node).getName().getFullyQualifiedName();
                name = packageName;
                break;
            case ASTNode.TYPE_DECLARATION:
                String className = ((TypeDeclaration) node).getName().getIdentifier();
                name = className;
                break;
            case ASTNode.FIELD_DECLARATION:
                FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
                String fieldName = fieldDeclaration.fragments().get(0).toString();
                name = fieldName;
                break;
            case ASTNode.METHOD_DECLARATION:
                String methodName = ((MethodDeclaration) node).getName().getIdentifier();
                name = methodName;
                break;
            default:
                // handle other node types here
                break;
        }
        return name;
    }

    public void setName(String className) {
    }

}
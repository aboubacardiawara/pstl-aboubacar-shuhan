package adaptation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import adaptation.interfaces.IAdapter;
import adaptation.interfaces.IRUASTNode;
import adaptation.interfaces.IRUAST;

/**
 * Le but de cette est d'adapter l'AST de JDT en notre struture
 * Cela nous permettra en effet de fusionner facilement les arbres.
 */
public class JDTtoRUASTAdapter extends ASTVisitor implements IAdapter {
    static Integer VARIANT_ID = 0;
    // Variant -> (null, {})

    public IRUAST adapt(CompilationUnit cu) {
        return null;
    }

    // declaration de type (visit :: ASTNode -> bool )
    {
        
    }
    // methode
    // Field
    // statement

}
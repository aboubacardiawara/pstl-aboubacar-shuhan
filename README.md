# EXTRACTION D'UNE LIGNE DE PRODUITS D'APPLICATIONS JAVA
Permet de générer la ligne de produit d'un ensemble de variants grâce à la manipulation d'AST. L'implementation de la génération des fichiers de configuration pour visualiser les fonctionnalités et leur rélation à l'aide de mobioos-forge est en cours. Seul un des deux fichiers est pour le moment généré (fm.forge).

# LANCEMENT
Le projet est construit à l'aide de maven. Suivez les etapes suivantes pour executer sur un ensemble de variants dont vous disposez.

## Configuration
Vous devez indiquer les chemins vers vos variants ainsi que le chemin vers le repertoire qui doit contenir le code de la ligne de produit.

### 1. Specifier les chemin de variants
Ouvrez le fichier configuration.json placé à la racine du projet.
Voici sa structure:

```json
{
    "variantsPaths": [
        "chemin variant1",
        "chemin variant2",
        ...
        "chemin variantn"
    ],
    "variantmax": "chemin de sortie"
}
```

### 2. Executer l'application
1. Installer les dependances si ce n'est pas déjà fait:
```sh
mvn clean install
```

2. Executer l'application
```sh
mvn exec:java
```
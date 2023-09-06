import Authorisation from "./Authorisation";
import Categorie from "./Categorie";
import Compagnie from "./Compagnie";
import Dossier from "./Dossier";
import Label from "./Label";

export default interface Fichier {
    id: number;
    nom: string;
    fullPath: string;
    racine : Dossier;
    compagnie: Compagnie;
    taille: number;
    extension: string;
    labels:Label[];
    categorie: Categorie;
    currentAuth:Authorisation;
}


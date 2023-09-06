import Authorisation from "./Authorisation";
import Compagnie from "./Compagnie";
import Fichier from "./Fichier";
import Groupe from "./Groupe";

export default interface Dossier {
    id: number;
    nom: string;
    racine: Dossier;
    compagnie: Compagnie;
    dossiers: Dossier[];
    fichiers: Fichier[];
    fullPath: string;
    groupe:Groupe;
    groupRoot: boolean;
    currentAuth:Authorisation;
}

import Compagnie from "./Compagnie";

export default interface Categorie {
    id: number;
    nom: string;
    compagnie: Compagnie;
}

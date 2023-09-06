import Compagnie from "./Compagnie";

export default interface Label {
    id: number;
    nom: string;
    compagnie: Compagnie;
}

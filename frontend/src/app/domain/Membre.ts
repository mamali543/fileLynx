import Groupe from "./Groupe";

export default interface Membre {
  id:number;
  username: string;
  email: string;
  nom: string;
  prenom: string;
  groupe: Groupe;
}

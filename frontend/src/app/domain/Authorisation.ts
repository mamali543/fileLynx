import Dossier from "./Dossier";
import RessourceAccessor from "./RessourceAccessor";

export default interface Authorisation {
  id: number;
  dossier: Dossier;
  lecture:boolean;
  ecriture:boolean;
  modification:boolean;
  suppression:boolean;
  partage:boolean;
  telechargement:boolean;
  upload:boolean;
  creationDossier:boolean;
  authLevel:string;
  ressourceAccessor:RessourceAccessor;
}

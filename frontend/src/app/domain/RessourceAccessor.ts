import Authorisation from "./Authorisation";

export default interface RessourceAccessor{
    id: number;
    authorisations: Authorisation[];
}

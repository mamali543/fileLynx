import Compagnie from "./Compagnie"

export default interface Groupe {
  id: number,
  nom: string,
  membres: string[]
  quota: number
  compagne: Compagnie
  quotaUsed: number
}

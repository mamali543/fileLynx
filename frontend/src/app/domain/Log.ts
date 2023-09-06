import Trigger from "./Trigger";
import Compagnie from "./Compagnie";

export default interface Log {
    id: number;
    message: String;
    type: string;
    date: Date;
    trigger: Trigger;
    compagnie: Compagnie;
}

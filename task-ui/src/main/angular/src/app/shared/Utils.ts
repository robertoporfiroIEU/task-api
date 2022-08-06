import { Assign, Spectator } from '../api';

export class Utils {
    static pCalendarDateFormat: string = 'dd/mm/yy';
    static datePipeDateFormat: string = 'dd/MM/yyyy';
    static datePipeDateTimeFormat: string = 'dd/MM/yyyy HH:mm';

    static isAssignsOrSpectatorsEquals(arr1: Assign[] | Spectator[], arr2: Assign[] | Spectator[]): boolean {

        let assignsOrSpectators1String: string[] = [];
        let assignsOrSpectators2String: string[] = [];

        arr1.forEach(assignOrSpectator => {
            if (assignOrSpectator.user) {
                assignsOrSpectators1String.push(assignOrSpectator.user);
            }

            if (assignOrSpectator.group) {
                assignsOrSpectators1String.push(assignOrSpectator.group);
            }
        });


        arr2.forEach(assignOrSpectator => {
            if (assignOrSpectator.user) {
                assignsOrSpectators2String.push(assignOrSpectator.user);
            }

            if (assignOrSpectator.group) {
                assignsOrSpectators2String.push(assignOrSpectator.group);
            }
        });

        if (assignsOrSpectators1String.length !== assignsOrSpectators2String.length) {
            return false;
        }

        return assignsOrSpectators1String.every( (e) => assignsOrSpectators2String.includes(e));

    }
}

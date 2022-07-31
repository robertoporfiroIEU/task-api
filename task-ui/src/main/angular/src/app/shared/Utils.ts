import { Assign, Spectator } from '../api';

export class Utils {
    static pCalendarDateFormat: string = 'dd/mm/yy';
    static datePipeDateFormat: string = 'dd/MM/yyyy';
    static datePipeDateTimeFormat: string = 'dd/MM/yyyy HH:mm';

    static getEnumFromStringValue(value: string, enumWithStringValues: any): any {
        return enumWithStringValues[Object.entries(enumWithStringValues).find(([key, val]) => val === value)?.[0]!];
    }

    static flatAssignsOrSpectators(assignsOrSpectators: Assign[] | Spectator[]): string[] {
        let flat: string[] = [];
        assignsOrSpectators.forEach(a => {
            if (a?.user) {
                flat.push(a.user?.name)
            }

            if (a?.group) {
                flat.push(a.group?.name)
            }
        })
        return flat;
    }


    static getAssignsFromArray(users: string[] | null, groups: string[] | null): Assign[] {
        let assigns: Assign[] = [];

        if (users) {
            let usersAssigns: string[] = [...users]
            usersAssigns.forEach((name: string) => {
                let assign: Assign = {
                    user: {
                        name: name
                    }
                }
                assigns.push(assign);
            });
        }

        if (groups) {
            let groupsAssigns: string[] = [...groups];
            groupsAssigns.forEach((name: string) => {
                let assign: Assign = {
                    group: {
                        name: name
                    }
                }
                assigns.push(assign);
            });
        }
        return assigns;
    }

    static getSpectatorsFromArray(users: string[] | null, groups: string[] | null): Spectator[] {
        let spectators: Spectator[] = [];

        if (users) {
            let usersSpectators: string[] = [...users];
            usersSpectators.forEach((name: string) => {
                let spectator: Spectator = {
                    user: {
                        name: name
                    }
                }
                spectators.push(spectator);
            });
        }

        if (groups) {
            let groupsSpectators: string[] = [...groups];
            groupsSpectators.forEach((name: string) => {
                let spectator: Spectator = {
                    group: {
                        name: name
                    }
                }
                spectators.push(spectator);
            });
        }

        return spectators;
    }

    static isAssignsOrSpectatorsEquals(arr1: Assign[] | Spectator[], arr2: Assign[] | Spectator[]): boolean {

        let assignsOrSpectators1String: string[] = [];
        let assignsOrSpectators2String: string[] = [];

        arr1.forEach(assignOrSpectator => {
            if (assignOrSpectator.user) {
                assignsOrSpectators1String.push(assignOrSpectator.user.name);
            }

            if (assignOrSpectator.group) {
                assignsOrSpectators1String.push(assignOrSpectator.group.name);
            }
        });


        arr2.forEach(assignOrSpectator => {
            if (assignOrSpectator.user) {
                assignsOrSpectators2String.push(assignOrSpectator.user.name);
            }

            if (assignOrSpectator.group) {
                assignsOrSpectators2String.push(assignOrSpectator.group.name);
            }
        });

        if (assignsOrSpectators1String.length !== assignsOrSpectators2String.length) {
            return false;
        }

        return assignsOrSpectators1String.every( (e) => assignsOrSpectators2String.includes(e));

    }
}

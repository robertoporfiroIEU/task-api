import { MenuItem } from "primeng/api";

export interface ApplicationMenuItem extends MenuItem {
    id: string;
    fontAwesomeClass: string,
    isSelected: boolean
}

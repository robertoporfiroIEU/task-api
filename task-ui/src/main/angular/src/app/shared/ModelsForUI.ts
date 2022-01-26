export interface Column {
    field: string; // This is the field name of the object. An example User: { name: 'test' } the field is the name
    header: string; // This is the translate key of the field.
    toolTipLabel?: string; // This is the translate key of the tooltip.
    placeholder?: string;
}

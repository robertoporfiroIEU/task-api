import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'shortText'
})
export class ShortTextPipe implements PipeTransform {
    transform(value: string, length: number): string {
        let words: string[] = value.split(' ');
        let shortedValue: string;

        if (words.length === 1) {
            shortedValue = value;
        } else {
            shortedValue = words.map(w => w.substr(0, 1)).join('');
        }

        return shortedValue.substr(0, length);
    }
}

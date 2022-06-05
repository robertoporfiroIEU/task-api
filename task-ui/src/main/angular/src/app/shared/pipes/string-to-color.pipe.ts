import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'stringToColor'
})
export class StringToColorPipe implements PipeTransform {
    taskColors: string[] = ['#4b4b4b', '#2196F3', '#40CB3BFF', '#8A3BCBFF', '#9416a6'];

    transform(value: string): string {
        return this.getColorFromStringValue(value, this.taskColors);
    }

    private getColorFromStringValue(value: string, colors: string[]): string {
        let hashNumber = this.generateHash(value);

        if (hashNumber < 0) {
            hashNumber = hashNumber * -1;
        }

        return colors[hashNumber % colors.length];
    }

    private generateHash(value: string): number {
        let hash = 0;
        if (value.length == 0)
            return hash;
        for (let i = 0; i < value.length; i++) {
            var charCode = value.charCodeAt(i);
            hash = ((hash << 7) - hash) + charCode;
            hash = hash & hash;
        }
        return hash;
    }

}

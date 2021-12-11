import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']
})
export class ShellComponent implements OnInit {

    @Input() title: string = '';
    @Input() homeUrl = '';
    @Input() logoImage: string = '';
    @Output() onLogout: EventEmitter<void> = new EventEmitter<void>();

    constructor() {}

    ngOnInit(): void {}

    logout(): void {
        this.onLogout.emit();
    }

}

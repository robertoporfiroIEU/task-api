import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import { ShellService } from './shell.service';
import { DOCUMENT } from '@angular/common';
import { RoutesEnum } from '../RoutesEnum';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']
})
export class ShellComponent implements OnInit {

    @Input() applicationTitle: string = '';
    @Input() homeUrl = '';
    @Input() logoImage: string = '';
    @Output() onLogout: EventEmitter<void> = new EventEmitter<void>();
    fullScreenMode: boolean = false;
    elem: any;

    loadingSpinner: boolean =  false;
    homePageURL: string =  '/' + RoutesEnum.projects.toString();

    constructor(
        private shellService: ShellService,
        @Inject(DOCUMENT) private document: any,
        private cd: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.shellService.onLoadingSpinner$.subscribe(state => {
            this.loadingSpinner = state;
            this.cd.detectChanges();
        });
        this.elem = document.documentElement;
        this.shellService.onFullScreenMode$.subscribe(state => {
            if (state) {
                if (this.elem.requestFullscreen) {
                    this.elem.requestFullscreen();
                } else if (this.elem.mozRequestFullScreen) {
                    /* Firefox */
                    this.elem.mozRequestFullScreen();
                } else if (this.elem.webkitRequestFullscreen) {
                    /* Chrome, Safari and Opera */
                    this.elem.webkitRequestFullscreen();
                } else if (this.elem.msRequestFullscreen) {
                    /* IE/Edge */
                    this.elem.msRequestFullscreen();
                }
            } else {
                if (document.fullscreenElement) {
                    if (this.document.exitFullscreen) {
                        this.document.exitFullscreen();
                    } else if (this.document.mozCancelFullScreen) {
                        /* Firefox */
                        this.document.mozCancelFullScreen();
                    } else if (this.document.webkitExitFullscreen) {
                        /* Chrome, Safari and Opera */
                        this.document.webkitExitFullscreen();
                    } else if (this.document.msExitFullscreen) {
                        /* IE/Edge */
                        this.document.msExitFullscreen();
                    }
                }
            }
            this.fullScreenMode = state;
            this.cd.detectChanges();
        });
    }

    logout(): void {
        this.onLogout.emit();
    }

}

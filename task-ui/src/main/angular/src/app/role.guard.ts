import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { RoutesEnum } from './RoutesEnum';

@Injectable({
    providedIn: 'root'
})
export class RoleGuard implements CanActivate {

    constructor(private authService: AuthService, private router: Router) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        let rolesAllowed = route.data['rolesAllowed'] as Array<string>;
        return this.authService.rolesAllowed(rolesAllowed).pipe(
            map(isAllowed => {
                if (isAllowed) {
                    return true;
                }
                return this.router.createUrlTree(['/' + RoutesEnum.unauthorised]);
            })
        )
    }

}

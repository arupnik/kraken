import {Inject, Injectable, InjectionToken} from '@angular/core';
import {ConfigurationService} from 'projects/commons/src/lib/config/configuration.service';

export const DOCKER_ROOT = new InjectionToken<string>('DockerRoot');
export const DOCKER_COMPOSE_ROOT = new InjectionToken<string>('DockerComposeRoot');

@Injectable()
export class DockerEndpointService {

  constructor(@Inject(DOCKER_ROOT) private _dockerRoot: string,
              @Inject(DOCKER_COMPOSE_ROOT) private _dockerComposeRoot: string,
              private config: ConfigurationService) {
  }

  dockerPath(suffix: string): string {
    return this.config.executorApiUrl(`${this._dockerRoot}${suffix}`);
  }

  dockerComposePath(suffix: string): string {
    return this.config.executorApiUrl(`${this._dockerComposeRoot}${suffix}`);
  }

  get dockerRoot(): string {
    return this._dockerRoot;
  }

  get dockerComposeRoot(): string {
    return this._dockerComposeRoot;
  }

}

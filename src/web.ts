import { WebPlugin } from '@capacitor/core';

import type { BackgroundstepPlugin, PermissionResultInterface, StepDataInterface, resultInterface } from './definitions';

export class BackgroundstepWeb
  extends WebPlugin
  implements BackgroundstepPlugin
{
	checkAndRequestPermission(): Promise<PermissionResultInterface> {
		throw new Error('Method not implemented.');
	}

	serviceStart(): Promise<resultInterface> {
		throw new Error('Method not implemented.');
	}

	serviceStop(): Promise<resultInterface> {
		throw new Error('Method not implemented.');
	}

	async echo(options: { value: string }): Promise<{ value: string }> {
		console.log('ECHO', options);
		return options;
	}

	async getToday(): Promise<StepDataInterface> {
		return {
			count: 0
		};
	};

	async getStepData(options: { sDateTime: string; eDateTime: string; }): Promise<StepDataInterface> {
		console.log('TERM', options);
		return {
			count: 0
		};
	}

}

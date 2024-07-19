export interface BackgroundstepPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  serviceStart(): Promise<resultInterface>;
  serviceStop(): Promise<resultInterface>;
  getToday(): Promise<StepDataInterface>;
  getStepData(term: { sDateTime: string, eDateTime: string }): Promise<StepDataInterface>;
  checkAndRequestPermission(): Promise<PermissionResultInterface>;
}

export interface StepDataInterface {
	count: number
}

export interface resultInterface {
	res: boolean
}

export interface PermissionResultInterface {
	granted: boolean;
}
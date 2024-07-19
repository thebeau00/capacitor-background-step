import { registerPlugin } from '@capacitor/core';

import type { BackgroundstepPlugin } from './definitions';

const Backgroundstep = registerPlugin<BackgroundstepPlugin>('Backgroundstep', {
  web: () => import('./web').then(m => new m.BackgroundstepWeb()),
});

export * from './definitions';
export { Backgroundstep };

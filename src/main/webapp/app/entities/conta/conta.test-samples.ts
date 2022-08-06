import { IConta, NewConta } from './conta.model';

export const sampleWithRequiredData: IConta = {
  id: 17789,
};

export const sampleWithPartialData: IConta = {
  id: 11281,
  dsConta: 'reboot',
  vlSaldo: 6212,
};

export const sampleWithFullData: IConta = {
  id: 11912,
  dsConta: 'Czech',
  vlSaldo: 99072,
};

export const sampleWithNewData: NewConta = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

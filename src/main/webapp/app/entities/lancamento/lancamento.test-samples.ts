import dayjs from 'dayjs/esm';

import { TipoLancamento } from 'app/entities/enumerations/tipo-lancamento.model';

import { ILancamento, NewLancamento } from './lancamento.model';

export const sampleWithRequiredData: ILancamento = {
  id: 79177,
};

export const sampleWithPartialData: ILancamento = {
  id: 34006,
  vlLancamento: 87451,
  dtLancamento: dayjs('2022-08-05'),
  tpLancamento: TipoLancamento['RECEITA'],
};

export const sampleWithFullData: ILancamento = {
  id: 29506,
  dsObservacao: 'Platinum Operations override',
  vlLancamento: 53772,
  dtLancamento: dayjs('2022-08-05'),
  tpLancamento: TipoLancamento['DESPESA'],
};

export const sampleWithNewData: NewLancamento = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

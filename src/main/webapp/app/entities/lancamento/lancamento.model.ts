import dayjs from 'dayjs/esm';
import { IConta } from 'app/entities/conta/conta.model';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { TipoLancamento } from 'app/entities/enumerations/tipo-lancamento.model';

export interface ILancamento {
  id: number;
  dsObservacao?: string | null;
  vlLancamento?: number | null;
  dtLancamento?: dayjs.Dayjs | null;
  tpLancamento?: TipoLancamento | null;
  conta?: Pick<IConta, 'id' | 'dsConta'> | null;
  categoria?: Pick<ICategoria, 'id' | 'dsCategoria'> | null;
}

export type NewLancamento = Omit<ILancamento, 'id'> & { id: null };

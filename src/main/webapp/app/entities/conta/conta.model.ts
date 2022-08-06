export interface IConta {
  id: number;
  dsConta?: string | null;
  vlSaldo?: number | null;
}

export type NewConta = Omit<IConta, 'id'> & { id: null };

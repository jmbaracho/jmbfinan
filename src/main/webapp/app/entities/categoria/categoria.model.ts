export interface ICategoria {
  id: number;
  dsCategoria?: string | null;
}

export type NewCategoria = Omit<ICategoria, 'id'> & { id: null };

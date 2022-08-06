import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ILancamento, NewLancamento } from '../lancamento.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILancamento for edit and NewLancamentoFormGroupInput for create.
 */
type LancamentoFormGroupInput = ILancamento | PartialWithRequiredKeyOf<NewLancamento>;

type LancamentoFormDefaults = Pick<NewLancamento, 'id'>;

type LancamentoFormGroupContent = {
  id: FormControl<ILancamento['id'] | NewLancamento['id']>;
  dsObservacao: FormControl<ILancamento['dsObservacao']>;
  vlLancamento: FormControl<ILancamento['vlLancamento']>;
  dtLancamento: FormControl<ILancamento['dtLancamento']>;
  tpLancamento: FormControl<ILancamento['tpLancamento']>;
  conta: FormControl<ILancamento['conta']>;
  categoria: FormControl<ILancamento['categoria']>;
};

export type LancamentoFormGroup = FormGroup<LancamentoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LancamentoFormService {
  createLancamentoFormGroup(lancamento: LancamentoFormGroupInput = { id: null }): LancamentoFormGroup {
    const lancamentoRawValue = {
      ...this.getFormDefaults(),
      ...lancamento,
    };
    return new FormGroup<LancamentoFormGroupContent>({
      id: new FormControl(
        { value: lancamentoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      dsObservacao: new FormControl(lancamentoRawValue.dsObservacao),
      vlLancamento: new FormControl(lancamentoRawValue.vlLancamento),
      dtLancamento: new FormControl(lancamentoRawValue.dtLancamento),
      tpLancamento: new FormControl(lancamentoRawValue.tpLancamento),
      conta: new FormControl(lancamentoRawValue.conta),
      categoria: new FormControl(lancamentoRawValue.categoria),
    });
  }

  getLancamento(form: LancamentoFormGroup): ILancamento | NewLancamento {
    return form.getRawValue() as ILancamento | NewLancamento;
  }

  resetForm(form: LancamentoFormGroup, lancamento: LancamentoFormGroupInput): void {
    const lancamentoRawValue = { ...this.getFormDefaults(), ...lancamento };
    form.reset(
      {
        ...lancamentoRawValue,
        id: { value: lancamentoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): LancamentoFormDefaults {
    return {
      id: null,
    };
  }
}

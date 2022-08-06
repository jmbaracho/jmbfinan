import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { LancamentoFormService, LancamentoFormGroup } from './lancamento-form.service';
import { ILancamento } from '../lancamento.model';
import { LancamentoService } from '../service/lancamento.service';
import { IConta } from 'app/entities/conta/conta.model';
import { ContaService } from 'app/entities/conta/service/conta.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';
import { TipoLancamento } from 'app/entities/enumerations/tipo-lancamento.model';

@Component({
  selector: 'jhi-lancamento-update',
  templateUrl: './lancamento-update.component.html',
})
export class LancamentoUpdateComponent implements OnInit {
  isSaving = false;
  lancamento: ILancamento | null = null;
  tipoLancamentoValues = Object.keys(TipoLancamento);

  contasSharedCollection: IConta[] = [];
  categoriasSharedCollection: ICategoria[] = [];

  editForm: LancamentoFormGroup = this.lancamentoFormService.createLancamentoFormGroup();

  constructor(
    protected lancamentoService: LancamentoService,
    protected lancamentoFormService: LancamentoFormService,
    protected contaService: ContaService,
    protected categoriaService: CategoriaService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareConta = (o1: IConta | null, o2: IConta | null): boolean => this.contaService.compareConta(o1, o2);

  compareCategoria = (o1: ICategoria | null, o2: ICategoria | null): boolean => this.categoriaService.compareCategoria(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lancamento }) => {
      this.lancamento = lancamento;
      if (lancamento) {
        this.updateForm(lancamento);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lancamento = this.lancamentoFormService.getLancamento(this.editForm);
    if (lancamento.id !== null) {
      this.subscribeToSaveResponse(this.lancamentoService.update(lancamento));
    } else {
      this.subscribeToSaveResponse(this.lancamentoService.create(lancamento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILancamento>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(lancamento: ILancamento): void {
    this.lancamento = lancamento;
    this.lancamentoFormService.resetForm(this.editForm, lancamento);

    this.contasSharedCollection = this.contaService.addContaToCollectionIfMissing<IConta>(this.contasSharedCollection, lancamento.conta);
    this.categoriasSharedCollection = this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(
      this.categoriasSharedCollection,
      lancamento.categoria
    );
  }

  protected loadRelationshipsOptions(): void {
    this.contaService
      .query()
      .pipe(map((res: HttpResponse<IConta[]>) => res.body ?? []))
      .pipe(map((contas: IConta[]) => this.contaService.addContaToCollectionIfMissing<IConta>(contas, this.lancamento?.conta)))
      .subscribe((contas: IConta[]) => (this.contasSharedCollection = contas));

    this.categoriaService
      .query()
      .pipe(map((res: HttpResponse<ICategoria[]>) => res.body ?? []))
      .pipe(
        map((categorias: ICategoria[]) =>
          this.categoriaService.addCategoriaToCollectionIfMissing<ICategoria>(categorias, this.lancamento?.categoria)
        )
      )
      .subscribe((categorias: ICategoria[]) => (this.categoriasSharedCollection = categorias));
  }
}

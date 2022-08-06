import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LancamentoFormService } from './lancamento-form.service';
import { LancamentoService } from '../service/lancamento.service';
import { ILancamento } from '../lancamento.model';
import { IConta } from 'app/entities/conta/conta.model';
import { ContaService } from 'app/entities/conta/service/conta.service';
import { ICategoria } from 'app/entities/categoria/categoria.model';
import { CategoriaService } from 'app/entities/categoria/service/categoria.service';

import { LancamentoUpdateComponent } from './lancamento-update.component';

describe('Lancamento Management Update Component', () => {
  let comp: LancamentoUpdateComponent;
  let fixture: ComponentFixture<LancamentoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let lancamentoFormService: LancamentoFormService;
  let lancamentoService: LancamentoService;
  let contaService: ContaService;
  let categoriaService: CategoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LancamentoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LancamentoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LancamentoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lancamentoFormService = TestBed.inject(LancamentoFormService);
    lancamentoService = TestBed.inject(LancamentoService);
    contaService = TestBed.inject(ContaService);
    categoriaService = TestBed.inject(CategoriaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Conta query and add missing value', () => {
      const lancamento: ILancamento = { id: 456 };
      const conta: IConta = { id: 71008 };
      lancamento.conta = conta;

      const contaCollection: IConta[] = [{ id: 83602 }];
      jest.spyOn(contaService, 'query').mockReturnValue(of(new HttpResponse({ body: contaCollection })));
      const additionalContas = [conta];
      const expectedCollection: IConta[] = [...additionalContas, ...contaCollection];
      jest.spyOn(contaService, 'addContaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lancamento });
      comp.ngOnInit();

      expect(contaService.query).toHaveBeenCalled();
      expect(contaService.addContaToCollectionIfMissing).toHaveBeenCalledWith(
        contaCollection,
        ...additionalContas.map(expect.objectContaining)
      );
      expect(comp.contasSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Categoria query and add missing value', () => {
      const lancamento: ILancamento = { id: 456 };
      const categoria: ICategoria = { id: 78741 };
      lancamento.categoria = categoria;

      const categoriaCollection: ICategoria[] = [{ id: 83622 }];
      jest.spyOn(categoriaService, 'query').mockReturnValue(of(new HttpResponse({ body: categoriaCollection })));
      const additionalCategorias = [categoria];
      const expectedCollection: ICategoria[] = [...additionalCategorias, ...categoriaCollection];
      jest.spyOn(categoriaService, 'addCategoriaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lancamento });
      comp.ngOnInit();

      expect(categoriaService.query).toHaveBeenCalled();
      expect(categoriaService.addCategoriaToCollectionIfMissing).toHaveBeenCalledWith(
        categoriaCollection,
        ...additionalCategorias.map(expect.objectContaining)
      );
      expect(comp.categoriasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lancamento: ILancamento = { id: 456 };
      const conta: IConta = { id: 49138 };
      lancamento.conta = conta;
      const categoria: ICategoria = { id: 43595 };
      lancamento.categoria = categoria;

      activatedRoute.data = of({ lancamento });
      comp.ngOnInit();

      expect(comp.contasSharedCollection).toContain(conta);
      expect(comp.categoriasSharedCollection).toContain(categoria);
      expect(comp.lancamento).toEqual(lancamento);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILancamento>>();
      const lancamento = { id: 123 };
      jest.spyOn(lancamentoFormService, 'getLancamento').mockReturnValue(lancamento);
      jest.spyOn(lancamentoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lancamento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lancamento }));
      saveSubject.complete();

      // THEN
      expect(lancamentoFormService.getLancamento).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(lancamentoService.update).toHaveBeenCalledWith(expect.objectContaining(lancamento));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILancamento>>();
      const lancamento = { id: 123 };
      jest.spyOn(lancamentoFormService, 'getLancamento').mockReturnValue({ id: null });
      jest.spyOn(lancamentoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lancamento: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lancamento }));
      saveSubject.complete();

      // THEN
      expect(lancamentoFormService.getLancamento).toHaveBeenCalled();
      expect(lancamentoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILancamento>>();
      const lancamento = { id: 123 };
      jest.spyOn(lancamentoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lancamento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lancamentoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareConta', () => {
      it('Should forward to contaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(contaService, 'compareConta');
        comp.compareConta(entity, entity2);
        expect(contaService.compareConta).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCategoria', () => {
      it('Should forward to categoriaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoriaService, 'compareCategoria');
        comp.compareCategoria(entity, entity2);
        expect(categoriaService.compareCategoria).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

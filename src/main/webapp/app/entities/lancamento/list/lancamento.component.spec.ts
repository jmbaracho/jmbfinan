import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { LancamentoService } from '../service/lancamento.service';

import { LancamentoComponent } from './lancamento.component';

describe('Lancamento Management Component', () => {
  let comp: LancamentoComponent;
  let fixture: ComponentFixture<LancamentoComponent>;
  let service: LancamentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'lancamento', component: LancamentoComponent }]), HttpClientTestingModule],
      declarations: [LancamentoComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(LancamentoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LancamentoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LancamentoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.lancamentos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to lancamentoService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getLancamentoIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getLancamentoIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

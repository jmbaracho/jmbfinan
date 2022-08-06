import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CategoriaService } from '../service/categoria.service';

import { CategoriaComponent } from './categoria.component';

describe('Categoria Management Component', () => {
  let comp: CategoriaComponent;
  let fixture: ComponentFixture<CategoriaComponent>;
  let service: CategoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'categoria', component: CategoriaComponent }]), HttpClientTestingModule],
      declarations: [CategoriaComponent],
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
      .overrideTemplate(CategoriaComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CategoriaComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CategoriaService);

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
    expect(comp.categorias?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to categoriaService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCategoriaIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCategoriaIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

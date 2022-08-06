import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'conta',
        data: { pageTitle: 'jmbFinanApp.conta.home.title' },
        loadChildren: () => import('./conta/conta.module').then(m => m.ContaModule),
      },
      {
        path: 'categoria',
        data: { pageTitle: 'jmbFinanApp.categoria.home.title' },
        loadChildren: () => import('./categoria/categoria.module').then(m => m.CategoriaModule),
      },
      {
        path: 'lancamento',
        data: { pageTitle: 'jmbFinanApp.lancamento.home.title' },
        loadChildren: () => import('./lancamento/lancamento.module').then(m => m.LancamentoModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

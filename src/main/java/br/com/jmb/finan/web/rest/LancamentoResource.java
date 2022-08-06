package br.com.jmb.finan.web.rest;

import br.com.jmb.finan.domain.Lancamento;
import br.com.jmb.finan.repository.LancamentoRepository;
import br.com.jmb.finan.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.jmb.finan.domain.Lancamento}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LancamentoResource {

    private final Logger log = LoggerFactory.getLogger(LancamentoResource.class);

    private static final String ENTITY_NAME = "lancamento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LancamentoRepository lancamentoRepository;

    public LancamentoResource(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    /**
     * {@code POST  /lancamentos} : Create a new lancamento.
     *
     * @param lancamento the lancamento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lancamento, or with status {@code 400 (Bad Request)} if the lancamento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lancamentos")
    public ResponseEntity<Lancamento> createLancamento(@RequestBody Lancamento lancamento) throws URISyntaxException {
        log.debug("REST request to save Lancamento : {}", lancamento);
        if (lancamento.getId() != null) {
            throw new BadRequestAlertException("A new lancamento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Lancamento result = lancamentoRepository.save(lancamento);
        return ResponseEntity
            .created(new URI("/api/lancamentos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lancamentos/:id} : Updates an existing lancamento.
     *
     * @param id the id of the lancamento to save.
     * @param lancamento the lancamento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lancamento,
     * or with status {@code 400 (Bad Request)} if the lancamento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lancamento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lancamentos/{id}")
    public ResponseEntity<Lancamento> updateLancamento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Lancamento lancamento
    ) throws URISyntaxException {
        log.debug("REST request to update Lancamento : {}, {}", id, lancamento);
        if (lancamento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lancamento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lancamentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Lancamento result = lancamentoRepository.save(lancamento);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lancamento.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lancamentos/:id} : Partial updates given fields of an existing lancamento, field will ignore if it is null
     *
     * @param id the id of the lancamento to save.
     * @param lancamento the lancamento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lancamento,
     * or with status {@code 400 (Bad Request)} if the lancamento is not valid,
     * or with status {@code 404 (Not Found)} if the lancamento is not found,
     * or with status {@code 500 (Internal Server Error)} if the lancamento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lancamentos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Lancamento> partialUpdateLancamento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Lancamento lancamento
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lancamento partially : {}, {}", id, lancamento);
        if (lancamento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lancamento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lancamentoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Lancamento> result = lancamentoRepository
            .findById(lancamento.getId())
            .map(existingLancamento -> {
                if (lancamento.getDsObservacao() != null) {
                    existingLancamento.setDsObservacao(lancamento.getDsObservacao());
                }
                if (lancamento.getVlLancamento() != null) {
                    existingLancamento.setVlLancamento(lancamento.getVlLancamento());
                }
                if (lancamento.getDtLancamento() != null) {
                    existingLancamento.setDtLancamento(lancamento.getDtLancamento());
                }
                if (lancamento.getTpLancamento() != null) {
                    existingLancamento.setTpLancamento(lancamento.getTpLancamento());
                }

                return existingLancamento;
            })
            .map(lancamentoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lancamento.getId().toString())
        );
    }

    /**
     * {@code GET  /lancamentos} : get all the lancamentos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lancamentos in body.
     */
    @GetMapping("/lancamentos")
    public List<Lancamento> getAllLancamentos(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Lancamentos");
        if (eagerload) {
            return lancamentoRepository.findAllWithEagerRelationships();
        } else {
            return lancamentoRepository.findAll();
        }
    }

    /**
     * {@code GET  /lancamentos/:id} : get the "id" lancamento.
     *
     * @param id the id of the lancamento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lancamento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lancamentos/{id}")
    public ResponseEntity<Lancamento> getLancamento(@PathVariable Long id) {
        log.debug("REST request to get Lancamento : {}", id);
        Optional<Lancamento> lancamento = lancamentoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(lancamento);
    }

    /**
     * {@code DELETE  /lancamentos/:id} : delete the "id" lancamento.
     *
     * @param id the id of the lancamento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lancamentos/{id}")
    public ResponseEntity<Void> deleteLancamento(@PathVariable Long id) {
        log.debug("REST request to delete Lancamento : {}", id);
        lancamentoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

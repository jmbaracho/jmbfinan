package br.com.jmb.finan.web.rest;

import static br.com.jmb.finan.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.jmb.finan.IntegrationTest;
import br.com.jmb.finan.domain.Lancamento;
import br.com.jmb.finan.domain.enumeration.TipoLancamento;
import br.com.jmb.finan.repository.LancamentoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LancamentoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LancamentoResourceIT {

    private static final String DEFAULT_DS_OBSERVACAO = "AAAAAAAAAA";
    private static final String UPDATED_DS_OBSERVACAO = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_VL_LANCAMENTO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VL_LANCAMENTO = new BigDecimal(2);

    private static final LocalDate DEFAULT_DT_LANCAMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DT_LANCAMENTO = LocalDate.now(ZoneId.systemDefault());

    private static final TipoLancamento DEFAULT_TP_LANCAMENTO = TipoLancamento.RECEITA;
    private static final TipoLancamento UPDATED_TP_LANCAMENTO = TipoLancamento.DESPESA;

    private static final String ENTITY_API_URL = "/api/lancamentos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Mock
    private LancamentoRepository lancamentoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLancamentoMockMvc;

    private Lancamento lancamento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lancamento createEntity(EntityManager em) {
        Lancamento lancamento = new Lancamento()
            .dsObservacao(DEFAULT_DS_OBSERVACAO)
            .vlLancamento(DEFAULT_VL_LANCAMENTO)
            .dtLancamento(DEFAULT_DT_LANCAMENTO)
            .tpLancamento(DEFAULT_TP_LANCAMENTO);
        return lancamento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lancamento createUpdatedEntity(EntityManager em) {
        Lancamento lancamento = new Lancamento()
            .dsObservacao(UPDATED_DS_OBSERVACAO)
            .vlLancamento(UPDATED_VL_LANCAMENTO)
            .dtLancamento(UPDATED_DT_LANCAMENTO)
            .tpLancamento(UPDATED_TP_LANCAMENTO);
        return lancamento;
    }

    @BeforeEach
    public void initTest() {
        lancamento = createEntity(em);
    }

    @Test
    @Transactional
    void createLancamento() throws Exception {
        int databaseSizeBeforeCreate = lancamentoRepository.findAll().size();
        // Create the Lancamento
        restLancamentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isCreated());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeCreate + 1);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getDsObservacao()).isEqualTo(DEFAULT_DS_OBSERVACAO);
        assertThat(testLancamento.getVlLancamento()).isEqualByComparingTo(DEFAULT_VL_LANCAMENTO);
        assertThat(testLancamento.getDtLancamento()).isEqualTo(DEFAULT_DT_LANCAMENTO);
        assertThat(testLancamento.getTpLancamento()).isEqualTo(DEFAULT_TP_LANCAMENTO);
    }

    @Test
    @Transactional
    void createLancamentoWithExistingId() throws Exception {
        // Create the Lancamento with an existing ID
        lancamento.setId(1L);

        int databaseSizeBeforeCreate = lancamentoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLancamentoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isBadRequest());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLancamentos() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        // Get all the lancamentoList
        restLancamentoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lancamento.getId().intValue())))
            .andExpect(jsonPath("$.[*].dsObservacao").value(hasItem(DEFAULT_DS_OBSERVACAO)))
            .andExpect(jsonPath("$.[*].vlLancamento").value(hasItem(sameNumber(DEFAULT_VL_LANCAMENTO))))
            .andExpect(jsonPath("$.[*].dtLancamento").value(hasItem(DEFAULT_DT_LANCAMENTO.toString())))
            .andExpect(jsonPath("$.[*].tpLancamento").value(hasItem(DEFAULT_TP_LANCAMENTO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLancamentosWithEagerRelationshipsIsEnabled() throws Exception {
        when(lancamentoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLancamentoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(lancamentoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLancamentosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(lancamentoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLancamentoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(lancamentoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        // Get the lancamento
        restLancamentoMockMvc
            .perform(get(ENTITY_API_URL_ID, lancamento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lancamento.getId().intValue()))
            .andExpect(jsonPath("$.dsObservacao").value(DEFAULT_DS_OBSERVACAO))
            .andExpect(jsonPath("$.vlLancamento").value(sameNumber(DEFAULT_VL_LANCAMENTO)))
            .andExpect(jsonPath("$.dtLancamento").value(DEFAULT_DT_LANCAMENTO.toString()))
            .andExpect(jsonPath("$.tpLancamento").value(DEFAULT_TP_LANCAMENTO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLancamento() throws Exception {
        // Get the lancamento
        restLancamentoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();

        // Update the lancamento
        Lancamento updatedLancamento = lancamentoRepository.findById(lancamento.getId()).get();
        // Disconnect from session so that the updates on updatedLancamento are not directly saved in db
        em.detach(updatedLancamento);
        updatedLancamento
            .dsObservacao(UPDATED_DS_OBSERVACAO)
            .vlLancamento(UPDATED_VL_LANCAMENTO)
            .dtLancamento(UPDATED_DT_LANCAMENTO)
            .tpLancamento(UPDATED_TP_LANCAMENTO);

        restLancamentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLancamento.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLancamento))
            )
            .andExpect(status().isOk());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getDsObservacao()).isEqualTo(UPDATED_DS_OBSERVACAO);
        assertThat(testLancamento.getVlLancamento()).isEqualByComparingTo(UPDATED_VL_LANCAMENTO);
        assertThat(testLancamento.getDtLancamento()).isEqualTo(UPDATED_DT_LANCAMENTO);
        assertThat(testLancamento.getTpLancamento()).isEqualTo(UPDATED_TP_LANCAMENTO);
    }

    @Test
    @Transactional
    void putNonExistingLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lancamento.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lancamento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lancamento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lancamento)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLancamentoWithPatch() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();

        // Update the lancamento using partial update
        Lancamento partialUpdatedLancamento = new Lancamento();
        partialUpdatedLancamento.setId(lancamento.getId());

        partialUpdatedLancamento
            .dsObservacao(UPDATED_DS_OBSERVACAO)
            .vlLancamento(UPDATED_VL_LANCAMENTO)
            .dtLancamento(UPDATED_DT_LANCAMENTO);

        restLancamentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLancamento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLancamento))
            )
            .andExpect(status().isOk());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getDsObservacao()).isEqualTo(UPDATED_DS_OBSERVACAO);
        assertThat(testLancamento.getVlLancamento()).isEqualByComparingTo(UPDATED_VL_LANCAMENTO);
        assertThat(testLancamento.getDtLancamento()).isEqualTo(UPDATED_DT_LANCAMENTO);
        assertThat(testLancamento.getTpLancamento()).isEqualTo(DEFAULT_TP_LANCAMENTO);
    }

    @Test
    @Transactional
    void fullUpdateLancamentoWithPatch() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();

        // Update the lancamento using partial update
        Lancamento partialUpdatedLancamento = new Lancamento();
        partialUpdatedLancamento.setId(lancamento.getId());

        partialUpdatedLancamento
            .dsObservacao(UPDATED_DS_OBSERVACAO)
            .vlLancamento(UPDATED_VL_LANCAMENTO)
            .dtLancamento(UPDATED_DT_LANCAMENTO)
            .tpLancamento(UPDATED_TP_LANCAMENTO);

        restLancamentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLancamento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLancamento))
            )
            .andExpect(status().isOk());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
        Lancamento testLancamento = lancamentoList.get(lancamentoList.size() - 1);
        assertThat(testLancamento.getDsObservacao()).isEqualTo(UPDATED_DS_OBSERVACAO);
        assertThat(testLancamento.getVlLancamento()).isEqualByComparingTo(UPDATED_VL_LANCAMENTO);
        assertThat(testLancamento.getDtLancamento()).isEqualTo(UPDATED_DT_LANCAMENTO);
        assertThat(testLancamento.getTpLancamento()).isEqualTo(UPDATED_TP_LANCAMENTO);
    }

    @Test
    @Transactional
    void patchNonExistingLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lancamento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lancamento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lancamento))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLancamento() throws Exception {
        int databaseSizeBeforeUpdate = lancamentoRepository.findAll().size();
        lancamento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLancamentoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lancamento))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lancamento in the database
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLancamento() throws Exception {
        // Initialize the database
        lancamentoRepository.saveAndFlush(lancamento);

        int databaseSizeBeforeDelete = lancamentoRepository.findAll().size();

        // Delete the lancamento
        restLancamentoMockMvc
            .perform(delete(ENTITY_API_URL_ID, lancamento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lancamento> lancamentoList = lancamentoRepository.findAll();
        assertThat(lancamentoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

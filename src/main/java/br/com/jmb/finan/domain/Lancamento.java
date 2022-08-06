package br.com.jmb.finan.domain;

import br.com.jmb.finan.domain.enumeration.TipoLancamento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Lancamento.
 */
@Entity
@Table(name = "lancamento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Lancamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ds_observacao")
    private String dsObservacao;

    @Column(name = "vl_lancamento", precision = 21, scale = 2)
    private BigDecimal vlLancamento;

    @Column(name = "dt_lancamento")
    private LocalDate dtLancamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_lancamento")
    private TipoLancamento tpLancamento;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lancamentos" }, allowSetters = true)
    private Conta conta;

    @ManyToOne
    @JsonIgnoreProperties(value = { "lancamentos" }, allowSetters = true)
    private Categoria categoria;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Lancamento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsObservacao() {
        return this.dsObservacao;
    }

    public Lancamento dsObservacao(String dsObservacao) {
        this.setDsObservacao(dsObservacao);
        return this;
    }

    public void setDsObservacao(String dsObservacao) {
        this.dsObservacao = dsObservacao;
    }

    public BigDecimal getVlLancamento() {
        return this.vlLancamento;
    }

    public Lancamento vlLancamento(BigDecimal vlLancamento) {
        this.setVlLancamento(vlLancamento);
        return this;
    }

    public void setVlLancamento(BigDecimal vlLancamento) {
        this.vlLancamento = vlLancamento;
    }

    public LocalDate getDtLancamento() {
        return this.dtLancamento;
    }

    public Lancamento dtLancamento(LocalDate dtLancamento) {
        this.setDtLancamento(dtLancamento);
        return this;
    }

    public void setDtLancamento(LocalDate dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public TipoLancamento getTpLancamento() {
        return this.tpLancamento;
    }

    public Lancamento tpLancamento(TipoLancamento tpLancamento) {
        this.setTpLancamento(tpLancamento);
        return this;
    }

    public void setTpLancamento(TipoLancamento tpLancamento) {
        this.tpLancamento = tpLancamento;
    }

    public Conta getConta() {
        return this.conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Lancamento conta(Conta conta) {
        this.setConta(conta);
        return this;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Lancamento categoria(Categoria categoria) {
        this.setCategoria(categoria);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lancamento)) {
            return false;
        }
        return id != null && id.equals(((Lancamento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lancamento{" +
            "id=" + getId() +
            ", dsObservacao='" + getDsObservacao() + "'" +
            ", vlLancamento=" + getVlLancamento() +
            ", dtLancamento='" + getDtLancamento() + "'" +
            ", tpLancamento='" + getTpLancamento() + "'" +
            "}";
    }
}

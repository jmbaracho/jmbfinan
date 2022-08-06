package br.com.jmb.finan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Conta.
 */
@Entity
@Table(name = "conta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Conta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ds_conta")
    private String dsConta;

    @Column(name = "vl_saldo", precision = 21, scale = 2)
    private BigDecimal vlSaldo;

    @OneToMany(mappedBy = "conta")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "conta", "categoria" }, allowSetters = true)
    private Set<Lancamento> lancamentos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Conta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsConta() {
        return this.dsConta;
    }

    public Conta dsConta(String dsConta) {
        this.setDsConta(dsConta);
        return this;
    }

    public void setDsConta(String dsConta) {
        this.dsConta = dsConta;
    }

    public BigDecimal getVlSaldo() {
        return this.vlSaldo;
    }

    public Conta vlSaldo(BigDecimal vlSaldo) {
        this.setVlSaldo(vlSaldo);
        return this;
    }

    public void setVlSaldo(BigDecimal vlSaldo) {
        this.vlSaldo = vlSaldo;
    }

    public Set<Lancamento> getLancamentos() {
        return this.lancamentos;
    }

    public void setLancamentos(Set<Lancamento> lancamentos) {
        if (this.lancamentos != null) {
            this.lancamentos.forEach(i -> i.setConta(null));
        }
        if (lancamentos != null) {
            lancamentos.forEach(i -> i.setConta(this));
        }
        this.lancamentos = lancamentos;
    }

    public Conta lancamentos(Set<Lancamento> lancamentos) {
        this.setLancamentos(lancamentos);
        return this;
    }

    public Conta addLancamento(Lancamento lancamento) {
        this.lancamentos.add(lancamento);
        lancamento.setConta(this);
        return this;
    }

    public Conta removeLancamento(Lancamento lancamento) {
        this.lancamentos.remove(lancamento);
        lancamento.setConta(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conta)) {
            return false;
        }
        return id != null && id.equals(((Conta) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Conta{" +
            "id=" + getId() +
            ", dsConta='" + getDsConta() + "'" +
            ", vlSaldo=" + getVlSaldo() +
            "}";
    }
}

package br.com.jmb.finan.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Categoria.
 */
@Entity
@Table(name = "categoria")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "ds_categoria")
    private String dsCategoria;

    @OneToMany(mappedBy = "categoria")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "conta", "categoria" }, allowSetters = true)
    private Set<Lancamento> lancamentos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Categoria id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDsCategoria() {
        return this.dsCategoria;
    }

    public Categoria dsCategoria(String dsCategoria) {
        this.setDsCategoria(dsCategoria);
        return this;
    }

    public void setDsCategoria(String dsCategoria) {
        this.dsCategoria = dsCategoria;
    }

    public Set<Lancamento> getLancamentos() {
        return this.lancamentos;
    }

    public void setLancamentos(Set<Lancamento> lancamentos) {
        if (this.lancamentos != null) {
            this.lancamentos.forEach(i -> i.setCategoria(null));
        }
        if (lancamentos != null) {
            lancamentos.forEach(i -> i.setCategoria(this));
        }
        this.lancamentos = lancamentos;
    }

    public Categoria lancamentos(Set<Lancamento> lancamentos) {
        this.setLancamentos(lancamentos);
        return this;
    }

    public Categoria addLancamento(Lancamento lancamento) {
        this.lancamentos.add(lancamento);
        lancamento.setCategoria(this);
        return this;
    }

    public Categoria removeLancamento(Lancamento lancamento) {
        this.lancamentos.remove(lancamento);
        lancamento.setCategoria(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categoria)) {
            return false;
        }
        return id != null && id.equals(((Categoria) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Categoria{" +
            "id=" + getId() +
            ", dsCategoria='" + getDsCategoria() + "'" +
            "}";
    }
}

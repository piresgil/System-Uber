package application.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_cartao")
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O tipo do cartão não pode estar vazio")
    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private TipoCartao tipo; // Exemplo: "Combustível", "Portagem"

    @NotNull(message = "O número não pode ser nulo")
    @Column(nullable = false, length = 100, unique = true)
    private String numero;

    @NotBlank(message = "O numero de Contrato não pode estar vazio")
    @Column(nullable = false, length = 100)
    private String contrato;

    @NotBlank(message = "O nome do carro não pode estar vazio")
    @Column(nullable = false, length = 100)
    private String nome;

    @OneToMany(mappedBy = "cartao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> despesas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "carro_id")
    private Carro carro;

    @Column(nullable = false)
    private boolean ativo = true; // Campo para soft delete

    @Version
    private Integer version; // Campo de versão para controle de concorrência

    public Cartao() {
    }

    public Cartao(Long id, TipoCartao tipo, String numero, String contrato, String nome, Carro carro) {
        this.id = id;
        this.tipo = tipo;
        this.numero = numero;
        this.contrato = contrato;
        this.nome = nome;
        this.carro = carro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoCartao getTipo() {
        return tipo;
    }

    public void setTipo(TipoCartao tipo) {
        this.tipo = tipo;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cartao cartao = (Cartao) o;
        return Objects.equals(id, cartao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cartao{" +
                "id=" + id +
                ", numero=" + numero +
                ", tipo='" + tipo + '\'' +
                ", contrato='" + contrato + '\'' +
                ", nome='" + nome + '\'' +
                ", carro=" + carro +
                '}';
    }

    public boolean isAtivo() {
        return ativo;
    }


    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Métodos para gerenciar a lista de despesas
    public void addDespesa(Despesa despesa) {
        despesas.add(despesa);
        despesa.setCartao(this);
    }

    public void removeDespesa(Despesa despesa) {
        despesas.remove(despesa);
        despesa.setCartao(null);
    }
}

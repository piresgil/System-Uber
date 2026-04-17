package application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidade Pagamento
 *
 * Representa um pagamento efetuado a um colaborador.
 * Agora inclui o tipo de pagamento através do enum TipoPagamento.
 */
@Entity
@Table(name = "tb_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Colaborador associado ao pagamento.
     * Relação ManyToOne — vários pagamentos podem pertencer ao mesmo colaborador.
     */
    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    /**
     * Plataforma usada para efetuar o pagamento (ex: MBWay, Transferência).
     */
    private String plataforma;

    /**
     * Data em que o pagamento foi realizado.
     */
    private LocalDate data;

    /**
     * Valor pago ao colaborador.
     */
    private Double valor;

    /**
     * Tipo de pagamento (semanal, mensal, por hora, fixo).
     */
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;
}

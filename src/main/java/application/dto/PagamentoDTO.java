package application.dto;

import application.model.TipoPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para a entidade Pagamento.
 *
 * Representa apenas os dados essenciais enviados/recebidos pela API.
 * O campo colaborador é representado pelo ID, evitando expor a entidade completa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {

    private Long id;

    /**
     * ID do colaborador associado ao pagamento.
     * (A entidade completa não deve ser exposta no DTO)
     */
    private Long colaboradorId;

    private TipoPagamento tipoPagamento;

    private String plataforma;
    private LocalDate data;
    private Double valor;
}

package application.dto;

import application.model.TipoCartao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para a entidade Cartao.
 *
 * Representa apenas os dados essenciais enviados/recebidos pela API.
 * Relações complexas são representadas apenas por IDs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartaoDTO {

    private Long id;

    /**
     * Tipo do cartão (COMBUSTIVEL, PORTAGEM, etc).
     */
    private TipoCartao tipo;

    /**
     * Número físico do cartão.
     */
    private String numero;

    /**
     * Número do contrato associado ao cartão.
     */
    private String contrato;

    /**
     * Nome associado ao cartão (ex: nome do carro).
     */
    private String nome;

    /**
     * ID do carro ao qual o cartão está associado.
     */
    private Long carroId;
}

package application.mappers;

import application.dto.CartaoDTO;
import application.model.Cartao;
import application.model.Carro;

/**
 * Mapper responsável por converter entre Cartao e CartaoDTO.
 *
 * O DTO utiliza apenas o ID do carro, enquanto a entidade
 * utiliza o objeto Carro completo.
 */
public class CartaoMapper {

    /**
     * Converte entidade → DTO.
     */
    public static CartaoDTO toDTO(Cartao entity) {
        if (entity == null) return null;

        return new CartaoDTO(
                entity.getId(),
                entity.getTipo(),
                entity.getNumero(),
                entity.getContrato(),
                entity.getNome(),
                entity.getCarro() != null ? entity.getCarro().getId() : null
        );
    }

    /**
     * Converte DTO → entidade.
     *
     * O carro NÃO é carregado aqui — apenas criamos um objeto com ID.
     * O CartaoController irá substituir este objeto pelo Carro real.
     */
    public static Cartao toEntity(CartaoDTO dto) {
        if (dto == null) return null;

        Cartao c = new Cartao();
        c.setId(dto.getId());
        c.setTipo(dto.getTipo());
        c.setNumero(dto.getNumero());
        c.setContrato(dto.getContrato());
        c.setNome(dto.getNome());

        // Apenas cria um carro com ID — o controller carrega o real
        if (dto.getCarroId() != null) {
            Carro carro = new Carro();
            carro.setId(dto.getCarroId());
            c.setCarro(carro);
        }

        return c;
    }
}

package application.mappers;

import application.dto.PagamentoDTO;
import application.model.Colaborador;
import application.model.Pagamento;

/**
 * Mapper responsável por converter entre Pagamento e PagamentoDTO.
 *
 * O DTO utiliza apenas o ID do colaborador e o enum TipoPagamento,
 * enquanto a entidade utiliza o objeto Colaborador completo.
 */
public class PagamentoMapper {

    /**
     * Converte entidade → DTO.
     * Extrai apenas o ID do colaborador.
     */
    public static PagamentoDTO toDTO(Pagamento entity) {
        if (entity == null) return null;

        return new PagamentoDTO(
                entity.getId(),
                entity.getColaborador() != null ? entity.getColaborador().getId() : null,
                entity.getTipoPagamento(),
                entity.getPlataforma(),
                entity.getData(),
                entity.getValor()
        );
    }

    /**
     * Converte DTO → entidade.
     *
     * O colaborador NÃO é carregado aqui — apenas criamos um objeto com ID.
     * O PagamentoService irá substituir este objeto por um Colaborador real.
     */
    public static Pagamento toEntity(PagamentoDTO dto) {
        if (dto == null) return null;

        Pagamento p = new Pagamento();
        p.setId(dto.getId());
        p.setTipoPagamento(dto.getTipoPagamento());
        p.setPlataforma(dto.getPlataforma());
        p.setData(dto.getData());
        p.setValor(dto.getValor());

        // Apenas cria um colaborador com ID — o service carrega o real
        if (dto.getColaboradorId() != null) {
            Colaborador c = new Colaborador();
            c.setId(dto.getColaboradorId());
            p.setColaborador(c);
        }

        return p;
    }
}

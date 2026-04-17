package application.services;

import application.model.Cartao;
import application.repositories.CartaoRepository;
import application.services.exceptions.DatabaseException;
import application.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço responsável pela gestão dos cartões no sistema.
 * Contém operações de busca, inserção, atualização e remoção de cartões.
 *
 * @author Daniel Gil
 */
@Service
public class CartaoService {

    private final CartaoRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(CartaoService.class);
    private final Map<Long, Cartao> cache = new HashMap<>();

    public void limparCache() {
        cache.clear();
    }

    /**
     * Construtor com injeção de dependência.
     */
    @Autowired
    public CartaoService(CartaoRepository repository) {
        this.repository = repository;
    }

    /**
     * Retorna todos os cartões cadastrados no banco de dados.
     */
    @Transactional(readOnly = true)
    public List<Cartao> findAll() {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache.values()); // Retorna os valores já no cache
        }

        List<Cartao> cartoes = repository.findAll();
        cartoes.forEach(cartao -> cache.put(cartao.getId(), cartao)); // Armazena no cache
        return cartoes;
    }

    /**
     * Busca um cartão pelo ID. Lança uma exceção caso não seja encontrado.
     *
     * @param id Identificador do cartão.
     * @return Cartão encontrado.
     * @throws ResourceNotFoundException Se o cartão não for encontrado.
     */
    @Transactional(readOnly = true)
    public Cartao findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cartão com ID {} não encontrado!", id);
                    return new ResourceNotFoundException(id);
                });
    }

    /**
     * Regista um novo cartão no banco de dados.
     *
     * @param obj Cartão a ser salvo.
     * @return O cartão salvo.
     * @throws DatabaseException Se ocorrer um erro ao salvar.
     */
    @Transactional
    public Cartao insert(@Valid Cartao obj) {
        try {
            Cartao savedCartao = repository.save(obj);
            cache.put(savedCartao.getId(), savedCartao); // Atualiza o cache
            logger.info("Cartão com ID {} cadastrado com sucesso.", savedCartao.getId());
            return savedCartao;
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao salvar cartão: {}", e.getMessage());
            throw new DatabaseException("Erro ao salvar o cartão: violação de integridade.");
        }
    }

    /**
     * Atualiza um cartão no banco de dados.
     *
     * @param id  Identificador do cartão a ser atualizado.
     * @param obj Dados do cartão atualizado.
     * @return O cartão atualizado.
     * @throws ResourceNotFoundException Se o cartão não for encontrado.
     */
    @Transactional
    public Cartao update(Long id, @Valid Cartao obj) {
        try {
            return repository.findById(id)
                    .map(entity -> {
                        // Atualiza os campos
                        entity.setTipo(obj.getTipo());
                        entity.setNumero(obj.getNumero());
                        entity.setNome(obj.getNome());
                        entity.setContrato(obj.getContrato());
                        entity.setCarro(obj.getCarro());

                        // Salva e retorna o carro atualizado
                        Cartao updateCartao = repository.save(entity);
                        logger.info("Cartão com ID {} atualizado com sucesso.", id);
                        return updateCartao;
                    })
                    .orElseThrow(() -> new ResourceNotFoundException(id));
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao atualizar cartão ID {}: {}", id, e.getMessage());
            throw new DatabaseException("Erro ao atualizar o cartão: violação de integridade.");
        }
    }

    /**
     * Remove um cartão pelo ID.
     *
     * @param id Identificador do cartão a ser removido.
     * @throws ResourceNotFoundException Se o cartão não for encontrado.
     * @throws DatabaseException         Se houver problemas de integridade referencial.
     */
    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
            cache.remove(id); // Remove do cache
            logger.info("Cartão com ID {} deletado com sucesso.", id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Tentativa de deletar cartão inexistente: ID {}", id);
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao excluir cartão ID {}: {}", id, e.getMessage());
            throw new DatabaseException("Não foi possível eliminar o cartão. Verifique dependências.");
        }
    }

    @Transactional
    public void softDeleteCarro(Long id) {
        Cartao cartao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartao não encontrado."));

        cartao.setAtivo(false); // Marca o carro como inativo
        repository.save(cartao);
    }
}

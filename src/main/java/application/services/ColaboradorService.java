/**
 * Serviço responsável por operações relacionadas à entidade Colaborador.
 *
 * @author Daniel Gil
 */
package application.services;

import application.model.Colaborador;
import application.repositories.ColaboradorRepository;
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
 * Classe de serviço responsável por operações de CRUD sobre Colaborador.
 * A anotação @Service indica que esta classe é um componente de serviço gerenciado pelo Spring.
 */
@Service
public class ColaboradorService {

    private static final Logger logger = LoggerFactory.getLogger(ColaboradorService.class); // Logger para registrar eventos
    private final Map<Long, Colaborador> cache = new HashMap<>();

    public void limparCache() {
        cache.clear();
    }

    // Injeção do repositório para acesso ao banco de dados
    @Autowired
    private ColaboradorRepository repository;

    /**
     * Retorna todos os colaboradores cadastrados.
     *
     * @return Lista de colaboradores.
     * A anotação @Transactional(readOnly = true) melhora a performance, evitando locks desnecessários.
     */
    @Transactional(readOnly = true)
    public List<Colaborador> findAll() {
        if (!cache.isEmpty()) {
            return new ArrayList<>(cache.values()); // Retorna os valores já no cache
        }

        List<Colaborador> colaboradores = repository.findAll();
        colaboradores.forEach(colaborador -> cache.put(colaborador.getId(), colaborador)); // Armazena no cache
        return colaboradores;
    }

    /**
     * Busca um colaborador pelo ID.
     *
     * @param id ID do colaborador.
     * @return O objeto Colaborador encontrado.
     * @throws ResourceNotFoundException Se o colaborador não for encontrado.
     */
    @Transactional(readOnly = true)
    public Colaborador findById(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            logger.error("Colaborador com ID {} não encontrado!", id);
            return new ResourceNotFoundException(id);
        });
    }

    /**
     * Insere um novo colaborador no banco de dados.
     *
     * @param obj Objeto Colaborador a ser salvo.
     * @return O colaborador salvo.
     * @throws DatabaseException Se ocorrer um erro de integridade de dados.
     */
    @Transactional
    public Colaborador insert(@Valid Colaborador obj) {
        try {
            Colaborador savedColaborador = repository.save(obj);
            cache.put(savedColaborador.getId(), savedColaborador);// Atualiza o cache
            logger.info("Colaborador com ID {} cadastrado com sucesso.", savedColaborador.getId());
            return savedColaborador;
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao salvar Colaborador: {}", e.getMessage());
            throw new DatabaseException("Erro ao salvar o Colaborador: violação de integridade.");
        }
    }

    /**
     * Atualiza um colaborador existente no banco de dados.
     *
     * @param id  ID do colaborador a ser atualizado.
     * @param obj Dados do colaborador para atualização.
     * @return O colaborador atualizado.
     * @throws ResourceNotFoundException Se o colaborador não for encontrado.
     * @throws DatabaseException         Se houver erro de integridade ao atualizar os dados.
     */
    @Transactional
    public Colaborador update(Long id, @Valid Colaborador obj) {
        try {
            return repository.findById(id)
                    .map(entity -> {
                        // Atualiza os campos
                        entity.setNome(obj.getNome());
                        entity.setEmail(obj.getEmail());
                        entity.setTelefone(obj.getTelefone());

                        // Salva e retorna o colçaborador atualizado
                        Colaborador updatedcolaborador = repository.save(entity);
                        logger.info("Colaborador com ID {} atualizado com sucesso.", id);
                        return updatedcolaborador;
                    })
                    .orElseThrow(() -> new ResourceNotFoundException(id));
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao atualizar carro ID {}: {}", id, e.getMessage());
            throw new DatabaseException("Erro ao atualizar o carro: violação de integridade.");
        }
    }

    /**
     * Exclui um colaborador pelo ID.
     *
     * @param id ID do colaborador a ser excluído.
     * @throws ResourceNotFoundException Se o colaborador não for encontrado.
     * @throws DatabaseException         Se houver erro de integridade ao excluir.
     */
    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
            cache.remove(id); // Remove do cache
            logger.info("Colaborador com ID {} deletado com sucesso.", id);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Tentativa de deletar Colaborador inexistente: ID {}", id);
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro ao excluir Colaborador ID {}: {}", id, e.getMessage());
            throw new DatabaseException("Não foi possível eliminar o Colaborador. Verifique dependências.");
        }
    }

    public Boolean existsByEmail(String email) {
        return repository.existsByEmail(email); // Chama o método do repositório para verificar duplicação de e-mail.
    }

    public Boolean existsByEmailAndIdNot(String email, Long id) {
        return repository.existsByEmailAndIdNot(email, id);
    }
}

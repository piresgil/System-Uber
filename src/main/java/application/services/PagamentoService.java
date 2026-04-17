package application.services;

import application.model.Colaborador;
import application.model.Pagamento;
import application.repositories.PagamentoRepository;
import application.services.exceptions.DatabaseException;
import application.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio da entidade Pagamento.
 *
 * Aqui é feita a validação, carregamento do colaborador,
 * tratamento de exceções e atualização dos dados.
 */
@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ColaboradorService colaboradorService;

    /**
     * Retorna todos os pagamentos.
     */
    public List<Pagamento> findAll() {
        return repository.findAll();
    }

    /**
     * Busca um pagamento pelo ID.
     * Lança exceção caso não exista.
     */
    public Pagamento findById(Long id) {
        Optional<Pagamento> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * Insere um novo pagamento.
     * Aqui carregamos o colaborador real usando o ID vindo do DTO.
     */
    public Pagamento insert(Pagamento obj) {

        // Carrega o colaborador real
        if (obj.getColaborador() != null && obj.getColaborador().getId() != null) {
            Colaborador col = colaboradorService.findById(obj.getColaborador().getId());
            obj.setColaborador(col);
        }

        return repository.save(obj);
    }

    /**
     * Remove um pagamento pelo ID.
     * Valida existência antes de remover.
     */
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException(id);
            }
            repository.deleteById(id);

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(id);

        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Atualiza um pagamento existente.
     */
    public Pagamento update(Long id, Pagamento obj) {
        try {
            Pagamento entity = repository.getReferenceById(id);

            // Atualiza os campos
            updateData(entity, obj);

            return repository.save(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    /**
     * Copia os dados permitidos do objeto recebido para o objeto persistido.
     */
    private void updateData(Pagamento entity, Pagamento obj) {

        // Atualiza colaborador se necessário
        if (obj.getColaborador() != null && obj.getColaborador().getId() != null) {
            Colaborador col = colaboradorService.findById(obj.getColaborador().getId());
            entity.setColaborador(col);
        }

        entity.setPlataforma(obj.getPlataforma());
        entity.setData(obj.getData());
        entity.setValor(obj.getValor());
    }
}

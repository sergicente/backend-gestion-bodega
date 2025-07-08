package cava.model.repository;

import cava.model.entity.Familia;
import cava.model.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

}

package cava.model.service;

import java.util.List;

public interface InterfaceGenericoCrud<E,ID> {

	E buscar(ID clave);
	List<E> buscarTodos();
	E insertar(E entidad);
	E modificar(E entidad);
	int borrar(ID clave);

}

/***
 * Controla a estado do cluster. 
 * Limpo: Disponível para uso. Sem informação residual
 * Liberado: Disponível para uso. Com informação residual
 * Ocupado: Indisponível para uso. 
 * 
 * @author Francisco Thiago
 *
 */

public enum Status {
	LIMPO, LIBERADO, OCUPADO;
}

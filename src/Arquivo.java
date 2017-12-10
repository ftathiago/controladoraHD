/***
 * 
 * @author Francisco Thiago
 *
 */

public class Arquivo {
	private String nome;
	private Integer tamanho;

	public Arquivo(String nome, Integer tamanho) {
		this.nome = nome;
		this.tamanho = tamanho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	public String toString() {
		return String.format("Arquivo: %s (Tamanho: %d)", this.nome, this.tamanho);
	}
}

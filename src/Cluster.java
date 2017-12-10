/***
 * 
 * @author Francisco Thiago
 *
 */

public class Cluster {
	private Arquivo arquivo;
	private Status status;
	private Cluster proximo;
	private Cluster continuacao;
	private PosicaoArquivo posicao;
	private int index;

	public Cluster(int index) {
		this.status = Status.LIMPO;
		this.proximo = null;
		this.continuacao = null;
		this.index = index;
	}

	public void Add(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Arquivo getArquivo() {
		return this.arquivo;
	}

	public void clear() {
		this.status = Status.LIMPO;
		this.posicao = null;
		this.arquivo = null;
		this.continuacao = null;
	}

	public void apagar() {
		this.status = Status.LIBERADO;
	}

	public Boolean disponivel() {
		return ((this.status == Status.LIBERADO) || (this.status == Status.LIMPO));
	}

	public Boolean isLimpo() {
		return this.status.equals(Status.LIMPO);
	}

	public Boolean isLiberado() {
		return this.status.equals(Status.LIBERADO);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getNome() {
		if (this.arquivo != null)
			return this.arquivo.getNome();
		else
			return "";
	}

	public Cluster getProximo() {
		return proximo;
	}

	public void setProximo(Cluster proximo) {
		this.proximo = proximo;
	}

	public Cluster getContinuacao() {
		return continuacao;
	}

	public void setContinuacao(Cluster continuacao) {
		this.continuacao = continuacao;
	}

	public int getIndex() {
		return this.index;
	}

	public PosicaoArquivo getPosicao() {
		return posicao;
	}

	public void setPosicao(PosicaoArquivo posicao) {
		this.posicao = posicao;
	}
	public boolean isBoF() {
		return this.posicao == PosicaoArquivo.BoF;
	}
	
	public boolean isEoF() {
		return this.posicao == PosicaoArquivo.EoF;
	}
}

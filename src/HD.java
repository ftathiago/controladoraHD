/***
 * 
 * @author Francisco Thiago
 *
 */

public class HD {
	private final Integer CAPACIDADE = 128;
	private Integer utilizado;
	private Cluster inicio;

	public HD() {
		this.utilizado = 0;
		inicio = new Cluster(1);
		Cluster atual = inicio;
		for (int i = 1; i < CAPACIDADE; i++) {
			Cluster novo = new Cluster(i + 1);
			atual.setProximo(novo);
			atual = novo;
		}
	}

	public void adicionar(Arquivo arquivo) {
		if (arquivo.getTamanho() > (this.CAPACIDADE - this.utilizado)) {
			System.out.printf("Não será possível incluir o arquivo %s. Ele não cabe no HD", arquivo.getNome());
			System.out.println("");
		} else {
			this.utilizado += arquivo.getTamanho();
			Cluster cluster = getClusterParaArquivo(inicio);
			/***
			 * Se mesmo assim não houver espaço suficiente livre para alocar o arquivo, você
			 * deve informar ao usuário que não foi possível criar o arquivo.
			 ***/
			if (cluster == null) {
				System.out.println("Não foi possível criar o arquivo!");
			} else {
				cluster.setContinuacao(reservar(arquivo, arquivo.getTamanho(), cluster));
				cluster.setStatus(Status.OCUPADO);
				cluster.setPosicao(PosicaoArquivo.BoF);
			}
		}
	}

	public void remover(Arquivo arquivo) {
		/***
		 * Para remover um arquivo, o usuário deve informar o nome do arquivo que deseja
		 * remover, e você deve então buscar o arquivo e liberar todos os seus nós.
		 ***/
		Cluster localizar = localizarArquivo(arquivo);
		while (localizar != null) {
			this.utilizado -= 1;
			Cluster aux = localizar.getContinuacao();
			localizar.setStatus(Status.LIBERADO);
			localizar.setPosicao(null);
			localizar.setContinuacao(null);

			localizar = aux;
		}
	}

	public void desfragmentar() {
		/***
		 * O usuário pode ainda solicitar a desfragmentação do HD. Nesse caso, você deve
		 * reorganizar os nós retirando os espaços liberados do meio da estrutura, e
		 * colocando-os agrupados ao final. Esses nós devem voltar a serem considerados
		 * limpos
		 ***/
		// Será a ponta da lista
		Cluster ponta = null;
		// Será o último nó ocupado.
		Cluster ultimoOcupado = null;

		// Encontra o último nó ocupado
		Cluster proc = this.inicio;
		while (proc != null) {
			if (proc.getStatus() == Status.OCUPADO) {
				ultimoOcupado = proc;
			}
			proc = proc.getProximo();
		}
		ponta = ultimoOcupado;

		// Varre toda a lista, desde o início até o último nó ocupado
		proc = this.inicio;
		Cluster ant = null;
		while ((proc != null) && (proc != ultimoOcupado)) {
			Cluster proximo = proc.getProximo();
			if (proc.getStatus() != Status.OCUPADO) {
				if (ant != null)
					ant.setProximo(proc.getProximo());
				else
					inicio = proximo;
				Cluster aux = ponta.getProximo();
				ponta.setProximo(proc);
				ponta = proc;
				ponta.setProximo(aux);
				ponta.clear();
			} else {
				ant = proc;
			}
			proc = proximo;
		}
		// Limpa o resto
		while (ponta != null) {
			if (ponta.getStatus() == Status.LIBERADO) {
				ponta.clear();
			}
			ponta = ponta.getProximo();
		}
	}

	public void ordenar() {
		/***
		 * Se o usuário solicitar que os arquivos sejam ordenados alfabeticamente, você
		 * deve reorganizar os arquivos com base em seus nomes.
		 ***/
		Cluster percorre = inicio;
		// Este bloco de código tem como função apenas colocar os BoF no início da pilha
		// Foi escrita apenas para conferência. É perfeitamente possível ordenar sem
		// este
		// primeiro bloco.
		Cluster ant = null;
		while (percorre != null) {
			if ((percorre.isBoF()) && (percorre != inicio)) {
				Cluster aux = percorre.getProximo();
				percorre.setProximo(inicio);
				inicio = percorre;
				if (ant != null)
					ant.setProximo(aux);
				percorre = aux;
			} else {
				ant = percorre;
				percorre = percorre.getProximo();
			}

		}
		// A ordenação de fato começa à partir daqui.
		percorre = inicio;

		Cluster aux = percorre, proximo;

		while (aux != null) {
			if (aux.isBoF()) {
				proximo = aux.getProximo();
				while ((proximo != null) && (!proximo.isBoF())) {
					proximo = proximo.getProximo();
				}
				Arquivo tmpArq;
				PosicaoArquivo tmpPos;
				Cluster tmpCont;
				while ((proximo != null)) {
					if (proximo.isBoF()) {
						if (aux.getNome().compareTo(proximo.getNome()) > 0) { // se vir depois
							tmpArq = aux.getArquivo();
							tmpPos = aux.getPosicao();
							tmpCont = aux.getContinuacao();
							// strcpy(aux->nome, t->nome);
							aux.Add(proximo.getArquivo());
							aux.setPosicao(proximo.getPosicao());
							aux.setContinuacao(proximo.getContinuacao());

							// strcpy(t->nome, s);
							proximo.Add(tmpArq);
							proximo.setPosicao(tmpPos);
							proximo.setContinuacao(tmpCont);

						}
					}
					proximo = proximo.getProximo();
				}
			}
			aux = aux.getProximo();
		}
	}

	public void imprimirHD() {
		Cluster proc = inicio;
		while (proc != null) {
			System.out.printf("Cluster[%03d] ", proc.getIndex());
			if (proc.getStatus() == Status.OCUPADO) {
				if (proc.getContinuacao() == null)
					System.out.printf("->[EOF] ");
				else
					System.out.printf("->[%03d] ", proc.getContinuacao().getIndex());
			} else {
				System.out.printf("------- ");
			}

			if (proc.getPosicao() != null)
				System.out.printf("%-5s", proc.getPosicao().toString());
			else
				System.out.print("     ");
			System.out.printf("Estado: %8s - Arquivo: %s - ", proc.getStatus().toString(), proc.getNome());

			System.out.println("");
			proc = proc.getProximo();
		}
		System.out.printf("Espaço disponível: %d", (this.CAPACIDADE - this.utilizado));
		System.out.println("");
	}

	private Cluster localizarArquivo(Arquivo arquivo) {
		Cluster proc = inicio;
		while (proc != null) {
			if ((proc.getStatus() == Status.OCUPADO) && (proc.getNome().equals(arquivo.getNome())) && (proc.isBoF())) {
				return proc;
			}
			proc = proc.getProximo();
		}
		return null;
	}

	private Cluster reservar(Arquivo arquivo, int tamanho, Cluster cluster) {
		cluster.Add(arquivo);
		cluster.setStatus(Status.OCUPADO);
		tamanho--;
		if (tamanho > 0) {
			Cluster novo = getClusterParaArquivo(cluster);
			novo.setContinuacao(reservar(arquivo, tamanho, novo));
			if (tamanho == 1)
				novo.setPosicao(PosicaoArquivo.EoF);
			return novo;
		} else {
			return null;
		}
	}

	private Cluster getClusterParaArquivo(Cluster aPartirDe) {
		Cluster cluster;
		Cluster local = aPartirDe;

		if (local == null) {
			local = this.inicio;
		}
		/***
		 * Inicialmente, você deve tentar alocar o arquivo em um espaço contínuo que
		 * esteja limpo
		 **/
		cluster = getProximoLimpo(local);
		if (cluster == null) {
			/***
			 * Se não houver, você deve tentar alocar o arquivo em um espaço contínuo que
			 * esteja liberado
			 */
			cluster = getProximoLiberado(local);
		}

		if (cluster == null) {
			/***
			 * Se não houver, você deve tentar alocar o arquivo particionando-o. Para isso,
			 * busque particionar o arquivo o mínimo possível. Nessa situação, nós limpos e
			 * liberados possuem a mesma prioridade, e a referência de continuação do
			 * arquivo do fim de um bloco deve receber o início do próximo bloco do arquivo
			 * particionado.
			 ***/
			return getClusterParaArquivo(this.inicio);
		}
		return cluster;
	}

	private Cluster getProximoLimpo(Cluster aPartirDe) {
		Cluster proc = aPartirDe;
		while ((proc != null) && (!proc.isLimpo())) {
			proc = proc.getProximo();
		}
		if ((proc != null) && (proc.isLimpo())) {
			return proc;
		} else {
			return null;
		}
	}

	private Cluster getProximoLiberado(Cluster aPartirDe) {
		Cluster proc = aPartirDe;
		while ((proc != null) && (!proc.isLiberado())) {
			proc = proc.getProximo();
		}
		if ((proc != null) && (proc.isLiberado())) {
			return proc;
		} else {
			return null;
		}
	}
}
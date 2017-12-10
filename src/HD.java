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
			System.out.printf("N�o ser� poss�vel incluir o arquivo %s. Ele n�o cabe no HD", arquivo.getNome());
			System.out.println("");
		} else {
			this.utilizado += arquivo.getTamanho();
			Cluster cluster = getClusterParaArquivo(inicio);
			/***
			 * Se mesmo assim n�o houver espa�o suficiente livre para alocar o arquivo, voc�
			 * deve informar ao usu�rio que n�o foi poss�vel criar o arquivo.
			 ***/
			if (cluster == null) {
				System.out.println("N�o foi poss�vel criar o arquivo!");
			} else {
				cluster.setContinuacao(reservar(arquivo, arquivo.getTamanho(), cluster));
				cluster.setStatus(Status.OCUPADO);
				cluster.setPosicao(PosicaoArquivo.BoF);
			}
		}
	}

	public void remover(Arquivo arquivo) {
		/***
		 * Para remover um arquivo, o usu�rio deve informar o nome do arquivo que deseja
		 * remover, e voc� deve ent�o buscar o arquivo e liberar todos os seus n�s.
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
		 * O usu�rio pode ainda solicitar a desfragmenta��o do HD. Nesse caso, voc� deve
		 * reorganizar os n�s retirando os espa�os liberados do meio da estrutura, e
		 * colocando-os agrupados ao final. Esses n�s devem voltar a serem considerados
		 * limpos
		 ***/
		// Ser� a ponta da lista
		Cluster ponta = null;
		// Ser� o �ltimo n� ocupado.
		Cluster ultimoOcupado = null;

		// Encontra o �ltimo n� ocupado
		Cluster proc = this.inicio;
		while (proc != null) {
			if (proc.getStatus() == Status.OCUPADO) {
				ultimoOcupado = proc;
			}
			proc = proc.getProximo();
		}
		ponta = ultimoOcupado;

		// Varre toda a lista, desde o in�cio at� o �ltimo n� ocupado
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
		 * Se o usu�rio solicitar que os arquivos sejam ordenados alfabeticamente, voc�
		 * deve reorganizar os arquivos com base em seus nomes.
		 ***/
		Cluster percorre = inicio;
		// Este bloco de c�digo tem como fun��o apenas colocar os BoF no in�cio da pilha
		// Foi escrita apenas para confer�ncia. � perfeitamente poss�vel ordenar sem
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
		// A ordena��o de fato come�a � partir daqui.
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
		System.out.printf("Espa�o dispon�vel: %d", (this.CAPACIDADE - this.utilizado));
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
		 * Inicialmente, voc� deve tentar alocar o arquivo em um espa�o cont�nuo que
		 * esteja limpo
		 **/
		cluster = getProximoLimpo(local);
		if (cluster == null) {
			/***
			 * Se n�o houver, voc� deve tentar alocar o arquivo em um espa�o cont�nuo que
			 * esteja liberado
			 */
			cluster = getProximoLiberado(local);
		}

		if (cluster == null) {
			/***
			 * Se n�o houver, voc� deve tentar alocar o arquivo particionando-o. Para isso,
			 * busque particionar o arquivo o m�nimo poss�vel. Nessa situa��o, n�s limpos e
			 * liberados possuem a mesma prioridade, e a refer�ncia de continua��o do
			 * arquivo do fim de um bloco deve receber o in�cio do pr�ximo bloco do arquivo
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
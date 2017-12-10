/***
 * 
 * @author Francisco Thiago
 *
 */

import java.util.Scanner;

public class HDManager {
	static Scanner teclado = new Scanner(System.in);

	private static void inserirArquivo(HD hd) {
		/***
		 * Para inserir um arquivo, o usuário informa o Nome do Arquivo e o seu tamanho,
		 * e você deve alocar esse arquivo no seu HD
		 ***/
		System.out.println("Informe o nome do arquivo");
		String nome = teclado.nextLine();

		System.out.println("Tamanho do arquivo");
		Integer tamanho = teclado.nextInt();

		hd.adicionar(new Arquivo(nome, tamanho));
	}

	public static void main(String[] args) {
		HD hd = new HD();
		int menu;

		do {
			System.out.println("Informe uma das opções:");
			System.out.println("1- Inserir arquivo");
			System.out.println("2- Remover arquivo");
			System.out.println("3- Desfragmentar");
			System.out.println("4- Ordenar");
			System.out.println("5- Visualizar");
			System.out.println("0- Sair");
			menu = teclado.nextInt();
			teclado.nextLine();

			switch (menu) {
			case 1:
				inserirArquivo(hd);
				break;
			case 2:
				System.out.println("Qual o nome do arquivo?");
				String nome = teclado.nextLine();
				Arquivo arquivo = new Arquivo(nome, 0);
				hd.remover(arquivo);
				break;
			case 3:
				hd.desfragmentar();
				break;
			case 4:
				hd.ordenar();
				break;
			case 5:
				hd.imprimirHD();
				break;
			}

		} while (menu != 0);
		System.out.println("Até logo!");
	}
}

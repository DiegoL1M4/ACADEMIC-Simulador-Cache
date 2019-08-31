package Cache;

public class Cache extends Dados {
	
	private int hit = 0;
	private int miss = 0;
	private int[][] cache;
	private int[][] acessos;
	private int j;
	
	public Cache(int tamanho, int bloco, int mapeamento, int metodo) {
		super(tamanho, bloco, mapeamento, metodo);
		
		double i = Math.pow(2,super.indexTam);
		double j = Math.pow(2,super.conjuntos);
		this.j = (int)j;
		cache = new int[(int)i][(int)j];
		acessos = new int[(int)i][(int)j];
	}
	
	public void entrada(String binario) {
		
		String p1 ="", p2 = "";
		
		// Divisão da palavra de 32 bits (tag | index | offset)
		for(int k = 0; k < 32; k++) {
			if(k<super.tagTam)
				p1 += binario.charAt(k);
			else if(k<super.tagTam + super.indexTam)
				p2 += binario.charAt(k);
		}
		int tag = Integer.parseInt(p1, 2);
		int index = Integer.parseInt(p2, 2);
		
		// Verifica se a tag está em um dos blocos
		int posicaoAcerto = 0;
		boolean verificacao = false;
		for(int k = 0; k < this.j; k++)
			if(cache[index][k] == tag) {
				verificacao = true;
				posicaoAcerto = k;
				break;
			}
		
		// Incrementa o hit ou o miss (aplica as políticas de substituição)
		if(verificacao == true && acessos[index][posicaoAcerto] != 0) {
			hit++;
			if(super.metodoSubstituicao == 1) {
				LRU(acessos, cache, index, tag, acessos[index][posicaoAcerto]);
			}else if(super.metodoSubstituicao == 2)
				acessos[index][posicaoAcerto]++;
		} else {
			miss++;
			if(super.metodoSubstituicao == 1) {
				LRU(acessos, cache, index, tag, -1);
			}else if(super.metodoSubstituicao == 2) {
				LFU(acessos, cache, index, tag);
			} else
				FIFO(acessos, cache, index, tag);
		}
		
		/*
		System.out.println(tag + " " + index);
		for(int k = 0; k < this.j; k++)
			System.out.printf("%d-",acessos[index][k]);
		System.out.println();
		for(int k = 0; k < this.j; k++)
			System.out.printf("%d-",cache[index][k]);
		System.out.println();
		System.out.println();
		*/
		
	}
	
	public void LRU(int[][] acessos, int[][] cache, int index, int tag, int posicao) {
		int maior = acessos[index][0];
		for(int k = 0; k < this.j; k++)
			if(acessos[index][k] > maior) 
				maior = acessos[index][k];
		
		if(posicao != -1) { // HIT
			if(maior == this.j) { // vetor completo
				int posVetor = 0;
				for(int k = 0; k < this.j; k++) {
					if(acessos[index][k] == posicao)
						posVetor = k;
					if(acessos[index][k] > posicao)
						acessos[index][k]--;
				}
				acessos[index][posVetor] = this.j;
			} else { // vetor incompleto
				int posVetor = 0;
				for(int k = 0; k < maior; k++) {
					if(acessos[index][k] == posicao)
						posVetor = k;
					if(acessos[index][k] > posicao)
						acessos[index][k]--;
				}
				acessos[index][posVetor] = maior;
			}
		} else { // MISS
			if(maior == this.j) { // vetor completo
				int bloco = 0;
				for(int k = 0; k < this.j; k++)
					acessos[index][k]--;
				for(int k = 0; k < this.j; k++)
					if(acessos[index][k] == 0) {
						acessos[index][k] = this.j;
						bloco = k;
						break;
					}
				cache[index][bloco] = tag;
			} else { // vetor incompleto
				for(int k = 0; k < this.j; k++)
					if(acessos[index][k] == 0) {
						cache[index][k] = tag;
						acessos[index][k] = maior + 1;
						break;
					}
			}
		}
		
	}
	
	public void LFU(int[][] acessos, int[][] cache, int index, int tag) {
		int posicao = 0, menor = acessos[index][0];
		for(int k = 0; k < this.j; k++)
			if(acessos[index][k] < menor) {
				menor = acessos[index][k];
				posicao = k;
			}
		acessos[index][posicao] = 1;
		cache[index][posicao] = tag;
		
	}
	
	public void FIFO(int[][] acessos, int[][] cache, int index, int tag) {
		int cont = 0;
		for(int k = 0; k < this.j; k++)
			if(acessos[index][k] == 0) {
				cache[index][k] = tag;
				acessos[index][k] = (k==0) ? (1) : (acessos[index][k-1] + 1);
				break;
			} else {
				cont++;
			}
		if(cont == this.j) {
			int bloco = 0;
			for(int k = 0; k < this.j; k++)
				acessos[index][k]--;
			for(int k = 0; k < this.j; k++)
				if(acessos[index][k] == 0) {
					acessos[index][k] = this.j;
					bloco = k;
					break;
				}
			cache[index][bloco] = tag;
		}
	}
	
	public String toString() {
		return "Acertos: " + this.hit + "\nErros: " + this.miss;
	}
	
}

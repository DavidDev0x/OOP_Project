import java.util.Random;

public class Tabuleiro {
    private boolean[][] bombas;
    private int tamanho;

    public Tabuleiro(int quantidadeBombas) {
        this.tamanho = 5;
        this.bombas = new boolean[tamanho][tamanho];
        colocarBombas(quantidadeBombas);
    }

    private void colocarBombas(int quantidadeBombas) {
        Random random = new Random();
        int bombasColocadas = 0;

        while (bombasColocadas < quantidadeBombas) {
            int linha = random.nextInt(tamanho);
            int coluna = random.nextInt(tamanho);
            if (!bombas[linha][coluna]) {
                bombas[linha][coluna] = true;
                bombasColocadas++;
            }
        }
    }

    public boolean revelarPosicao(int linha, int coluna) {
        return bombas[linha][coluna];
    }

}

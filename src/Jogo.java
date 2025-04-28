abstract class Jogo {
    protected Jogador jogador;

    public Jogo() {
        jogador = new Jogador();
    }

    public abstract void iniciarJogo();

    public abstract void jogar();
}

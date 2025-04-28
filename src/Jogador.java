public class Jogador {
    private double saldo;
    private double saldoDeposito;
    private double ganhos;

    public Jogador() {
        this.saldo = 0;
        this.saldoDeposito = 0;
        this.ganhos = 0;
    }

    public void depositar(double valor) {
        this.saldoDeposito += valor;
        this.saldo += valor;
    }

    public void depositar(double valor, String mensagem) {
        this.saldoDeposito += valor;
        this.saldo += valor;
        System.out.println(mensagem);
    }

    public boolean apostar(double valor) {
        if (valor <= saldo) {
            return true;
        }
        return false;
    }

    public void perderAposta(double valor) {
        saldo -= valor;
    }

    public void adicionarGanhos(double valor) {
        ganhos += valor;
        saldo += valor;
    }

    public void retirarGanhos() {
        saldoDeposito += ganhos;
        ganhos = 0;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getSaldoDeposito() {
        return saldoDeposito;
    }
}

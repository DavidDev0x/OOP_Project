import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.text.DecimalFormat;

public class JogoMinesGUI extends JFrame {
    private JButton[][] botoes;
    private Tabuleiro tabuleiro;
    private Jogador jogador;
    private int quantidadeBombas;
    private double valorAposta, ganhosAcumulados = 0.0;
    private JLabel labelSaldo, labelSaldoDeposito, labelMensagens;
    private boolean jogoEmAndamento = true;

    private ImageIcon bombaIcon = new ImageIcon("src/images/bomba2.png");
    private ImageIcon diamanteIcon = new ImageIcon("src/images/diamante 2.png");
    private BufferedImage fundoImage;  // Imagem de fundo
    private DecimalFormat formatoMonetario = new DecimalFormat("0.00");

    public JogoMinesGUI() {
        jogador = new Jogador();
        carregarImagemFundo();
        inicializarComponentes();
    }

    private void carregarImagemFundo() {
        try {
            fundoImage = ImageIO.read(new File("src/images/fundo.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarComponentes() {
        setTitle("Jogo Mines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (fundoImage != null) {
                    g.drawImage(fundoImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        painelFundo.setLayout(new BorderLayout());
        painelFundo.setOpaque(true);
        JPanel painelJogo = new JPanel();
        painelJogo.setLayout(new GridLayout(5, 5, 5, 5));
        painelJogo.setOpaque(false);
        painelFundo.add(painelJogo, BorderLayout.CENTER);
        botoes = new JButton[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton botao = new JButton();
                botao.setPreferredSize(new Dimension(80, 80));
                int linha = i;
                int coluna = j;
                botao.setBackground(new Color(218, 165, 32));
                botao.setForeground(Color.BLACK);
                botao.addActionListener(e -> {
                    if (jogoEmAndamento) {
                        revelarPosicao(linha, coluna, botao);
                    }
                });
                botoes[i][j] = botao;
                painelJogo.add(botao);
            }
        }

        JPanel painelBordas = new JPanel();
        painelBordas.setLayout(new BorderLayout());
        painelBordas.add(Box.createHorizontalStrut(400), BorderLayout.WEST);
        painelBordas.add(painelJogo, BorderLayout.CENTER);
        painelBordas.add(Box.createHorizontalStrut(400), BorderLayout.EAST);
        painelBordas.setOpaque(false);

        JPanel painelControle = new JPanel();
        painelControle.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelControle.setOpaque(false);
        JLabel labelDeposito = new JLabel("Depósito Inicial: ");
        labelDeposito.setForeground(Color.white);
        labelDeposito.setFont(new Font("Impact", Font.PLAIN, 16));
        JTextField campoDeposito = new JTextField(5);
        campoDeposito.setPreferredSize(new Dimension(100, 30));

        JLabel labelAposta = new JLabel("Valor da Aposta: ");
        labelAposta.setFont(new Font("Impact", Font.PLAIN, 16));
        labelAposta.setForeground(Color.white);
        JTextField campoAposta = new JTextField(5);
        campoAposta.setPreferredSize(new Dimension(100, 30));

        JButton botaoDepositar = new JButton("Depositar");
        botaoDepositar.setPreferredSize(new Dimension(120, 40));
        botaoDepositar.setBackground(new Color(139, 69, 19));
        botaoDepositar.setForeground(Color.WHITE);
        botaoDepositar.addActionListener(e -> {
            try {
                double valorDeposito = Double.parseDouble(campoDeposito.getText());
                if (valorDeposito <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor do depósito deve ser positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                jogador.depositar(valorDeposito);
                atualizarSaldos();
                labelMensagens.setText("Depósito de R$ " + formatoMonetario.format(valorDeposito) + " realizado.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido para depósito.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botaoApostar = new JButton("Apostar");
        botaoApostar.setPreferredSize(new Dimension(120, 40));
        botaoApostar.setBackground(new Color(139, 69, 19));
        botaoApostar.setForeground(Color.WHITE);
        botaoApostar.addActionListener(e -> {
            try {
                valorAposta = Double.parseDouble(campoAposta.getText());
                if (valorAposta <= 0) {
                    JOptionPane.showMessageDialog(this, "O valor da aposta deve ser positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (jogador.apostar(valorAposta)) {
                    iniciarJogo();
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente para a aposta.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido para a aposta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton botaoRetirar = new JButton("Retirar Ganhos");
        botaoRetirar.setPreferredSize(new Dimension(150, 40));
        botaoRetirar.setBackground(new Color(222, 184, 135));
        botaoRetirar.setForeground(Color.white);
        botaoRetirar.addActionListener(e -> {
            if (ganhosAcumulados > 0) {
                jogador.adicionarGanhos(ganhosAcumulados);
                double ganhosRetirados = ganhosAcumulados;
                ganhosAcumulados = 0.0;
                atualizarSaldos();
                labelMensagens.setText("Ganhos de R$ " + formatoMonetario.format(ganhosRetirados) + " retirados com sucesso.");
            } else {
                labelMensagens.setText("Não há ganhos acumulados para retirar.");
            }
            limparTabuleiro();
        });

        painelControle.add(labelDeposito);
        painelControle.add(campoDeposito);
        painelControle.add(botaoDepositar);
        painelControle.add(labelAposta);
        painelControle.add(campoAposta);
        painelControle.add(botaoApostar);
        painelControle.add(botaoRetirar);

        JPanel painelInformacoes = new JPanel();
        painelInformacoes.setLayout(new GridLayout(3, 1, 10, 10));
        painelInformacoes.setOpaque(false);

        Font fonte = new Font("Arial", Font.BOLD, 20);
        Color corTexto = Color.WHITE;

        labelSaldo = new JLabel("Saldo: R$ 0.00", JLabel.CENTER);
        labelMensagens = new JLabel("Bem-vindo ao Jogo Mines!", JLabel.CENTER);
        labelSaldoDeposito = new JLabel("Saldo Depositado: R$ 0.00", JLabel.CENTER);

        labelSaldo.setFont(fonte);
        labelSaldo.setForeground(corTexto);

        labelSaldoDeposito.setFont(fonte);
        labelSaldoDeposito.setForeground(corTexto);

        labelMensagens.setFont(fonte);
        labelMensagens.setForeground(corTexto);

        painelInformacoes.add(labelSaldo);
        painelInformacoes.add(labelSaldoDeposito);
        painelInformacoes.add(labelMensagens);

        painelFundo.add(painelBordas, BorderLayout.CENTER);
        painelFundo.add(painelControle, BorderLayout.SOUTH);
        painelFundo.add(painelInformacoes, BorderLayout.NORTH);

        add(painelFundo);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
    }

    private void iniciarJogo() {
        jogoEmAndamento = true;
        quantidadeBombas = escolherQuantidadeBombas();
        tabuleiro = new Tabuleiro(quantidadeBombas);
        limparTabuleiro();
        labelMensagens.setText("Jogo iniciado! Boa sorte.");
    }

    private int escolherQuantidadeBombas() {
        String input = JOptionPane.showInputDialog("Digite a quantidade de bombas (3 a 20):");
        try {
            int quantidade = Integer.parseInt(input);
            if (quantidade < 3 || quantidade > 20) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida. Definindo 5 bombas por padrão.");
                return 5;
            }
            return quantidade;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Entrada inválida. Definindo 5 bombas por padrão.");
            return 5;
        }
    }

    private void revelarTabuleiro() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (tabuleiro.revelarPosicao(i, j)) {
                    botoes[i][j].setIcon(bombaIcon);
                } else {
                    botoes[i][j].setIcon(diamanteIcon);
                }
            }
        }
    }

    private void revelarPosicao(int linha, int coluna, JButton botao) {
        if (tabuleiro.revelarPosicao(linha, coluna)) {
            botao.setIcon(bombaIcon);
            JOptionPane.showMessageDialog(this, "Você caiu no conto do vigário!", "Derrota", JOptionPane.WARNING_MESSAGE);
            jogador.perderAposta(valorAposta);
            ganhosAcumulados = 0.0;
            atualizarSaldos();
            revelarTabuleiro();
            jogoEmAndamento = false;
        } else {
            botao.setIcon(diamanteIcon);
            botao.setEnabled(false);
            botao.setFocusable(false);
            int quantidadeSeguraAberta = contarCelulasSegurasAbertas();
            double ganho = calcularGanho(valorAposta, quantidadeSeguraAberta, quantidadeBombas);
            ganhosAcumulados += ganho;
            labelMensagens.setText("Você ganhou R$ " + formatoMonetario.format(ganhosAcumulados) + ". Continue jogando ou retire?");
            atualizarSaldos();
        }
    }

    private int contarCelulasSegurasAbertas() {
        int contador = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (botoes[i][j].getIcon() == diamanteIcon) {
                    contador++;
                }
            }
        }
        return contador;
    }

    private double calcularGanho(double aposta, int quantidadeSeguraAberta, int quantidadeBombas) {
        double multiplicadorBase = 0.2;
        double multiplicadorPorCelulaSegura = 0.002;
        double multiplicadorPorBomba = 0.03 * quantidadeBombas;
        double multiplicadorTotal = multiplicadorBase + (quantidadeSeguraAberta * multiplicadorPorCelulaSegura) + multiplicadorPorBomba;
        double ganho = aposta * multiplicadorTotal;
        return ganho;
    }

    private void atualizarSaldos() {
        labelSaldo.setText("Saldo: R$ " + formatoMonetario.format(jogador.getSaldo()));
        labelSaldoDeposito.setText("Saldo Depositado: R$ " + formatoMonetario.format(jogador.getSaldoDeposito()));
    }

    private void limparTabuleiro() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                botoes[i][j].setIcon(null);
                botoes[i][j].setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JogoMinesGUI jogo = new JogoMinesGUI();
            jogo.setVisible(true);
        });
    }
}
